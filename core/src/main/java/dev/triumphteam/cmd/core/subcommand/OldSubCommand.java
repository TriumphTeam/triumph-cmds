/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.subcommand;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotations.Default;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.command.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.processor.OldAbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.requirement.Requirement;
import dev.triumphteam.cmd.core.extention.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SubCommand implementation.
 * Might be better to rename this to something different.
 *
 * @param <S> The sender type.
 */
public abstract class OldSubCommand<S> {

    private final BaseCommand baseCommand;
    private final Method method = null;

    private final String parentName;
    private final String name;
    private final List<String> alias;
    private final boolean isDefault;

    private final Class<? extends S> senderType;

    private final List<InternalArgument<S, ?>> internalArguments;
    private final Set<Requirement<S, ?>> requirements;

    private final MessageRegistry<S> messageRegistry;
    private final ExecutionProvider executionProvider;

    private final SenderValidator<S> senderValidator;

    private final boolean hasArguments;
    private final boolean containsLimitless;

    public OldSubCommand(
            @NotNull final OldAbstractSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        this.baseCommand = processor.getBaseCommand();
        // this.method = processor.getAnnotatedElement();
        this.name = processor.getName();
        this.alias = processor.getAlias();
        this.internalArguments = processor.getArguments();
        this.requirements = processor.getRequirements();
        this.messageRegistry = processor.getMessageRegistry();
        this.isDefault = processor.isDefault();
        this.senderValidator = processor.getSenderValidator();

        this.senderType = processor.getSenderType();

        this.parentName = parentName;

        this.executionProvider = executionProvider;

        this.hasArguments = !internalArguments.isEmpty();
        this.containsLimitless = internalArguments.stream().anyMatch(LimitlessInternalArgument.class::isInstance);
    }

    /**
     * Checks if the sub command is default.
     * Can also just check if the name is {@link Default#DEFAULT_CMD_NAME}.
     *
     * @return Whether the sub command is default.
     */
    public boolean isDefault() {
        return isDefault;
    }

    // TODO: 2/5/2022 comments
    @NotNull public Class<? extends S> getSenderType() {
        return senderType;
    }

    /**
     * Gets the name of the parent command.
     *
     * @return The name of the parent command.
     */
    @NotNull public String getParentName() {
        return parentName;
    }

    /**
     * Gets the name of the sub command.
     *
     * @return The name of the sub command.
     */
    @NotNull public String getName() {
        return name;
    }

    public boolean hasArguments() {
        return hasArguments;
    }

    /**
     * Gets the message registry.
     *
     * @return The message registry.
     */
    @NotNull protected MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    /**
     * Executes the sub command.
     *
     * @param sender The sender.
     * @param args   The arguments to pass to the executor.
     */
    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        if (!senderValidator.validate(messageRegistry, this, sender)) return;
        if (!meetRequirements(sender)) return;

        // Creates the invoking arguments list
        final List<java.lang.Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        if (!validateAndCollectArguments(sender, invokeArguments, args)) {
            return;
        }

        if ((!containsLimitless) && args.size() >= invokeArguments.size()) {
            messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new DefaultMessageContext(parentName, name));
            return;
        }

        executionProvider.execute(() -> {
            try {
                method.invoke(baseCommand, invokeArguments.toArray());
            } catch (IllegalAccessException | InvocationTargetException exception) {
                throw new CommandExecutionException("An error occurred while executing the command", parentName, name)
                        .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
            }
        });
    }

    /**
     * Gets the arguments of the sub command.
     *
     * @return The arguments of the sub command.
     */
    @NotNull protected List<InternalArgument<S, ?>> getArguments() {
        return internalArguments;
    }

    @Nullable protected InternalArgument<S, ?> getArgument(@NotNull final String name) {
        final List<InternalArgument<S, ?>> foundArgs = internalArguments.stream()
                .filter(internalArgument -> internalArgument.getName().toLowerCase().startsWith(name))
                .collect(Collectors.toList());

        if (foundArgs.size() != 1) return null;
        return foundArgs.get(0);
    }

    @Nullable protected InternalArgument<S, ?> getArgument(final int index) {
        final int size = internalArguments.size();
        if (size == 0) return null;
        if (index >= size) {
            final InternalArgument<S, ?> last = internalArguments.get(size - 1);
            if (last instanceof LimitlessInternalArgument) return last;
            return null;
        }

        return internalArguments.get(index);
    }

    // TODO: 2/1/2022 Comments
    public List<@Nullable String> mapArguments(@NotNull final Map<String, String> args) {
        final List<String> arguments = getArguments().stream().map(InternalArgument::getName).collect(Collectors.toList());
        return arguments.stream().map(it -> {
            final String value = args.get(it);
            return value == null ? "" : value;
        }).collect(Collectors.toList());
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
            @NotNull final S sender,
            @NotNull final List<java.lang.Object> invokeArguments,
            @NotNull final List<String> commandArgs
    ) {
        for (int i = 0; i < internalArguments.size(); i++) {
            final InternalArgument<S, ?> internalArgument = internalArguments.get(i);

            if (internalArgument instanceof LimitlessInternalArgument) {
                final LimitlessInternalArgument<S> limitlessArgument = (LimitlessInternalArgument<S>) internalArgument;
                final List<String> leftOvers = leftOvers(commandArgs, i);

                final java.lang.Object result = limitlessArgument.resolve(sender, leftOvers);

                if (result == null) {
                    return false;
                }

                invokeArguments.add(result);
                return true;
            }

            if (!(internalArgument instanceof StringInternalArgument)) {
                throw new CommandExecutionException("Found unsupported internalArgument", parentName, name);
            }

            final StringInternalArgument<S> stringArgument = (StringInternalArgument<S>) internalArgument;
            final String arg = valueOrNull(commandArgs, i);

            if (arg == null || arg.isEmpty()) {
                if (internalArgument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }

                messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new DefaultMessageContext(parentName, name));
                return false;
            }

            final java.lang.Object result = stringArgument.resolve(sender, arg);
            if (result == null) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        new InvalidArgumentContext(parentName, name, arg, internalArgument.getName(), internalArgument.getType())
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
    private boolean meetRequirements(@NotNull final S sender) {
        for (final Requirement<S, ?> requirement : requirements) {
            if (!requirement.isMet(sender)) {
                requirement.sendMessage(messageRegistry, sender, parentName, name);
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
    @Nullable private String valueOrNull(@NotNull final List<String> list, final int index) {
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
    @NotNull private List<String> leftOvers(@NotNull final List<String> list, final int from) {
        if (from > list.size()) return Collections.emptyList();
        return list.subList(from, list.size());
    }

    @NotNull @Override
    public String toString() {
        return "SimpleSubCommand{" +
                "baseCommand=" + baseCommand +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", alias=" + alias +
                ", isDefault=" + isDefault +
                ", arguments=" + internalArguments +
                ", requirements=" + requirements +
                ", messageRegistry=" + messageRegistry +
                ", containsLimitlessArgument=" + containsLimitless +
                '}';
    }
}
