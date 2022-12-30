package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.processor.ParentCommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A parent sub command is basically a holder of other sub commands.
 * This normally represents an inner class of a main command.
 * It can contain arguments which will turn it into an argument-as-subcommand type.
 *
 * @param <S> The sender type to be used.
 */
public class ParentSubCommand<S> implements ParentCommand<S>, ExecutableCommand<S> {

    private final Map<String, ExecutableCommand<S>> commands = new HashMap<>();
    private final Map<String, ExecutableCommand<S>> commandAliases = new HashMap<>();
    private final String name;
    private final CommandMeta meta;
    private final BaseCommand baseCommand;
    private final Constructor<?> constructor;
    private final boolean isStatic;
    private final StringInternalArgument<S> argument;
    private final boolean hasArgument;
    private final MessageRegistry<S> messageRegistry;

    // Single parent command with argument
    private ExecutableCommand<S> parentCommandWithArgument;

    public ParentSubCommand(
            final @NotNull BaseCommand baseCommand,
            final @NotNull Constructor<?> constructor,
            final boolean isStatic,
            final @Nullable StringInternalArgument<S> argument,
            final @NotNull ParentCommandProcessor<S> processor
    ) {
        this.baseCommand = baseCommand;
        this.constructor = constructor;
        this.isStatic = isStatic;
        this.argument = argument;
        this.hasArgument = argument != null;

        this.name = processor.getName();
        this.meta = processor.createMeta();
        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
    }

    @Override
    public void addSubCommand(final @NotNull ExecutableCommand<S> subCommand, final boolean isAlias) {
        // If it's a parent command with argument we add it
        if (subCommand instanceof ParentSubCommand && subCommand.hasArguments()) {
            if (parentCommandWithArgument != null) {
                throw new CommandRegistrationException("Only one inner command with argument is allowed per command", baseCommand.getClass());
            }

            parentCommandWithArgument = subCommand;
            return;
        }

        // Normal commands are added here
        commands.put(subCommand.getName(), subCommand);
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @NotNull String command,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull List<String> arguments
    ) {
        final int argumentSize = arguments.size();

        final String commandName = nameFromArguments(arguments);
        final ExecutableCommand<S> subCommand = getSubCommand(commandName, argumentSize);

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new DefaultMessageContext(name, commandName));
            return;
        }

        try {
            final Object argumentValue;
            if (hasArgument) argumentValue = argument.resolve(sender, command);
            else argumentValue = null;

            if (hasArgument && argumentValue == null) {
                // TODO INVALID ARGUMENT HERE
                return;
            }

            final Object instance = createInstance(instanceSupplier, argumentValue);
            subCommand.execute(sender, commandName, () -> instance, !subCommand.isDefault() ? arguments.subList(1, argumentSize) : arguments);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @param argumentValue    The value of the argument to pass if there is an argument.
     * @return An instance of this command for execution.
     */
    private @NotNull Object createInstance(
            final @Nullable Supplier<Object> instanceSupplier,
            final @Nullable Object argumentValue
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!isStatic) {
            // If there is no argument don't pass anything
            // A bit annoying but if the method has no parameters, "null" is a valid parameter so this check is needed
            if (!hasArgument) {
                return constructor.newInstance(instanceSupplier == null ? baseCommand : instanceSupplier.get());
            }

            return constructor.newInstance(instanceSupplier == null ? baseCommand : instanceSupplier.get(), argumentValue);
        }

        if (!hasArgument) constructor.newInstance();
        return constructor.newInstance(argumentValue);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull BaseCommand getBaseCommand() {
        return baseCommand;
    }

    @Override
    public @Nullable ExecutableCommand<S> getParentCommandWithArgument() {
        return parentCommandWithArgument;
    }

    @Override
    public boolean hasArguments() {
        return argument != null;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
