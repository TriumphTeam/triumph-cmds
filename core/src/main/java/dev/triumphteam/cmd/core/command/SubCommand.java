package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.BasicMessageContext;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SubCommand<S> implements ExecutableCommand<S> {

    private final Class<? extends S> senderType;
    private final List<InternalArgument<S, ?>> arguments;
    private final List<Requirement<S, ?>> requirements;

    private final String name;
    private final CommandMeta meta;
    private final boolean containsLimitless;

    private final Object invocationInstance;
    private final Method method;

    private final SenderExtension<?, S> senderExtension;
    private final MessageRegistry<S> messageRegistry;

    public SubCommand(
            final @NotNull Object invocationInstance,
            final @NotNull Method method,
            final @NotNull SubCommandProcessor<S> processor
    ) {
        this.invocationInstance = invocationInstance;
        this.method = method;
        this.name = processor.getName();
        this.meta = processor.createMeta();
        this.senderType = processor.senderType();
        this.arguments = processor.arguments(meta);
        this.requirements = processor.requirements();

        this.containsLimitless = arguments.stream().anyMatch(LimitlessInternalArgument.class::isInstance);

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = processor.getCommandExtensions().getSenderExtension();
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @NotNull String command,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull List<String> commandPath,
            final @NotNull List<String> arguments
    ) throws InvocationTargetException, IllegalAccessException {
        if (!senderExtension.validate(messageRegistry, this, sender)) return;
        if (!meetRequirements(sender, commandPath, arguments)) return;

        // Creates the invoking arguments list
        final List<java.lang.Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        if (!validateAndCollectArguments(sender, commandPath, invokeArguments, arguments)) {
            return;
        }

        if ((!containsLimitless) && arguments.size() >= invokeArguments.size()) {
            messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new BasicMessageContext(commandPath, arguments));
            return;
        }

        method.invoke(instanceSupplier == null ? invocationInstance : instanceSupplier.get(), invokeArguments.toArray());
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Object getInvocationInstance() {
        return invocationInstance;
    }

    @Override
    public boolean isDefault() {
        return name.equals(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME);
    }

    @Override
    public boolean hasArguments() {
        return !arguments.isEmpty();
    }

    /**
     * Used for checking if the arguments are valid and adding them to the `invokeArguments`.
     *
     * @param sender          The sender of the command.
     * @param invokeArguments A list with the arguments that'll be used on the `invoke` of the command method.
     * @param commandArgs     The command arguments type.
     * @return False if any internalArgument fails to pass.
     */
    @SuppressWarnings("unchecked")
    private boolean validateAndCollectArguments(
            final @NotNull S sender,
            final @NotNull List<String> commandPath,
            final @NotNull List<Object> invokeArguments,
            final @NotNull List<String> commandArgs
    ) {
        for (int i = 0; i < arguments.size(); i++) {
            final InternalArgument<S, ?> internalArgument = arguments.get(i);

            if (internalArgument instanceof LimitlessInternalArgument) {
                final LimitlessInternalArgument<S> limitlessArgument = (LimitlessInternalArgument<S>) internalArgument;
                final List<String> leftOvers = leftOvers(commandArgs, i);

                final Object result = limitlessArgument.resolve(sender, leftOvers);

                if (result == null) {
                    return false;
                }

                invokeArguments.add(result);
                return true;
            }

            if (!(internalArgument instanceof StringInternalArgument)) {
                throw new CommandExecutionException("Found unsupported argument", "", name);
            }

            final StringInternalArgument<S> stringArgument = (StringInternalArgument<S>) internalArgument;
            final String arg = valueOrNull(commandArgs, i);

            if (arg == null || arg.isEmpty()) {
                if (internalArgument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }

                messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new BasicMessageContext(commandPath, commandArgs));
                return false;
            }

            final Object result = stringArgument.resolve(sender, arg);
            if (result == null) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        new InvalidArgumentContext(commandPath, commandArgs, arg, internalArgument.getName(), internalArgument.getType())
                );
                return false;
            }

            invokeArguments.add(result);
        }

        return true;
    }

    /**
     * Checks if the requirements to run the command are met.
     *
     * @param sender The sender of the command.
     * @return Whether all requirements are met.
     */
    private boolean meetRequirements(
            final @NotNull S sender,
            final @NotNull List<String> commandPath,
            final @NotNull List<String> argumentPath
    ) {
        for (final Requirement<S, ?> requirement : requirements) {
            if (!requirement.isMet(sender)) {
                requirement.sendMessage(messageRegistry, sender, commandPath, argumentPath);
                return false;
            }
        }

        return true;
    }

    /**
     * Gets an internalArgument value or null.
     *
     * @param list  The list to check from.
     * @param index The current index of the internalArgument.
     * @return The internalArgument name or null.
     */
    private @Nullable String valueOrNull(final @NotNull List<String> list, final int index) {
        if (index >= list.size()) return null;
        return list.get(index);
    }

    /**
     * Gets the left over of the arguments.
     *
     * @param list The list with all the arguments.
     * @param from The index from which should start removing.
     * @return A list with the leftover arguments.
     */
    private @NotNull List<String> leftOvers(final @NotNull List<String> list, final int from) {
        if (from > list.size()) return Collections.emptyList();
        return list.subList(from, list.size());
    }
}
