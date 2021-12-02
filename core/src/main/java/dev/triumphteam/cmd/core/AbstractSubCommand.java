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
package dev.triumphteam.cmd.core;

import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.argument.Argument;
import dev.triumphteam.cmd.core.argument.FlagArgument;
import dev.triumphteam.cmd.core.argument.LimitlessArgument;
import dev.triumphteam.cmd.core.argument.StringArgument;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.flag.internal.result.InvalidFlagArgumentResult;
import dev.triumphteam.cmd.core.flag.internal.result.ParseResult;
import dev.triumphteam.cmd.core.flag.internal.result.RequiredArgResult;
import dev.triumphteam.cmd.core.flag.internal.result.RequiredFlagsResult;
import dev.triumphteam.cmd.core.flag.internal.result.SuccessResult;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.InvalidFlagArgumentContext;
import dev.triumphteam.cmd.core.message.context.MissingFlagArgumentContext;
import dev.triumphteam.cmd.core.message.context.MissingFlagContext;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * SubCommand implementation.
 * Might be better to rename this to something different.
 *
 * @param <S> The sender type.
 */
public abstract class AbstractSubCommand<S> implements SubCommand<S> {

    private final BaseCommand baseCommand;
    private final Method method;

    private final String parentName;
    private final String name;
    private final List<String> alias;
    private final boolean isDefault;

    private final List<Argument<S, ?>> arguments;
    private final Set<Requirement<S, ?>> requirements;

    private final MessageRegistry<S> messageRegistry;
    private final ExecutionProvider executionProvider;

    private boolean containsLimitless = false;
    private boolean containsFlags = false;

    public AbstractSubCommand(
            @NotNull final AbstractSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        this.baseCommand = processor.getBaseCommand();
        this.method = processor.getMethod();
        this.name = processor.getName();
        this.alias = processor.getAlias();
        this.arguments = processor.getArguments();
        this.requirements = processor.getRequirements();
        this.messageRegistry = processor.getMessageRegistry();
        this.isDefault = processor.isDefault();

        this.parentName = parentName;

        this.executionProvider = executionProvider;

        checkArguments();
    }

    /**
     * Checks if the sub command is default.
     * Can also just check if the name is {@link Default#DEFAULT_CMD_NAME}.
     *
     * @return Whether the sub command is default.
     */
    @Override
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Executes the sub command.
     *
     * @param sender The sender.
     * @param args   The arguments to pass to the executor.
     */
    @Override
    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        if (!meetRequirements(sender)) return;

        // Creates the invoking arguments list
        final List<Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        if (!validateAndCollectArguments(sender, invokeArguments, args)) {
            return;
        }

        if ((!containsLimitless && !containsFlags) && args.size() >= invokeArguments.size()) {
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
    protected List<Argument<S, ?>> getArguments() {
        return arguments;
    }

    /**
     * Used for checking if the arguments are valid and adding them to the `invokeArguments`.
     *
     * @param sender          The sender of the command.
     * @param invokeArguments A list with the arguments that'll be used on the `invoke` of the command method.
     * @param commandArgs     The command arguments type.
     * @return False if any argument fails to pass.
     */
    @SuppressWarnings("unchecked")
    private boolean validateAndCollectArguments(
            @NotNull final S sender,
            @NotNull final List<Object> invokeArguments,
            @NotNull final List<String> commandArgs
    ) {
        for (int i = 0; i < arguments.size(); i++) {
            final Argument<S, ?> argument = arguments.get(i);

            if (argument instanceof LimitlessArgument) {
                final LimitlessArgument<S> limitlessArgument = (LimitlessArgument<S>) argument;
                final List<String> leftOvers = leftOvers(commandArgs, i);

                return handleLimitless(limitlessArgument, sender, invokeArguments, leftOvers, i);
            }

            if (!(argument instanceof StringArgument)) {
                throw new CommandExecutionException("Found unsupported argument", parentName, name);
            }

            final StringArgument<S> stringArgument = (StringArgument<S>) argument;
            final String arg = valueOrNull(commandArgs, i);

            if (arg == null) {
                if (argument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }

                messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new DefaultMessageContext(parentName, name));
                return false;
            }

            final Object result = stringArgument.resolve(sender, arg);
            if (result == null) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        new InvalidArgumentContext(parentName, name, arg, argument.getName(), argument.getType())
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
     * Handles all types of {@link LimitlessArgument}s.
     *
     * TODO: 10/9/2021 Not very happy with the current implementation of handling List + Flags arguments
     *  but it work for now, definitely need to change this before full release.
     *
     * @param argument        The current limitless argument.
     * @param sender          The sender for resolution.
     * @param invokeArguments The list with invoke arguments to add new values to.
     * @param args            The current arguments to parse.
     * @return Whether the parsing was successful or not.
     */
    private boolean handleLimitless(
            @NotNull final LimitlessArgument<S> argument,
            @NotNull final S sender,
            @NotNull final List<Object> invokeArguments,
            @NotNull final List<String> args,
            final int index
    ) {
        if (!containsFlags) {
            invokeArguments.add(argument.resolve(sender, args));
            return true;
        }

        final ParseResult<S> result;
        if (containsLimitless) {
            //noinspection unchecked
            final LimitlessArgument<S> tempArg = (LimitlessArgument<S>) arguments.get(index + 1);
            result = getFlagResult(tempArg, sender, args);
        } else {
            result = getFlagResult(argument, sender, args);
        }

        if (result instanceof RequiredFlagsResult) {
            messageRegistry.sendMessage(MessageKey.MISSING_REQUIRED_FLAG, sender, new MissingFlagContext(parentName, name, (RequiredFlagsResult<?>) result));
            return false;
        }

        if (result instanceof RequiredArgResult) {
            messageRegistry.sendMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT, sender, new MissingFlagArgumentContext(parentName, name, (RequiredArgResult<?>) result));
            return false;
        }

        if (result instanceof InvalidFlagArgumentResult) {
            messageRegistry.sendMessage(MessageKey.INVALID_FLAG_ARGUMENT, sender, new InvalidFlagArgumentContext(parentName, name, (InvalidFlagArgumentResult<?>) result));
            return false;
        }

        // Should never happen
        if (!(result instanceof SuccessResult)) {
            throw new CommandExecutionException("Error occurred while parsing command flags", parentName, name);
        }

        final SuccessResult<S> successResult = (SuccessResult<S>) result;
        if (containsLimitless) {
            invokeArguments.add(argument.resolve(sender, successResult.getLeftOvers()));
        }

        invokeArguments.add(successResult.getFlags());
        return true;
    }

    /**
     * Simply gets the parsed flags from the argument
     *
     * @param argument The argument to check if it's a flag argument which should always be.
     * @param sender   The sender of the command.
     * @param args     The remaining arguments typed.
     * @return The {@link ParseResult} of the flags.
     */
    private ParseResult<S> getFlagResult(
            @NotNull final LimitlessArgument<S> argument,
            @NotNull final S sender,
            @NotNull final List<String> args
    ) {
        if (!(argument instanceof FlagArgument)) {
            throw new CommandExecutionException("An error occurred while handling command flags", parentName, name);
        }
        final FlagArgument<S> flagArgument = (FlagArgument<S>) argument;
        return flagArgument.resolve(sender, args);
    }

    /**
     * Gets an argument value or null.
     *
     * @param list  The list to check from.
     * @param index The current index of the argument.
     * @return The argument name or null.
     */
    @Nullable
    private String valueOrNull(@NotNull final List<String> list, final int index) {
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
    @NotNull
    private List<String> leftOvers(@NotNull final List<String> list, final int from) {
        if (from > list.size()) return Collections.emptyList();
        return list.subList(from, list.size());
    }

    /**
     * Checks and records if the arguments contain Flags or/and LimitlessArguments.
     */
    private void checkArguments() {
        for (final Argument<S, ?> argument : arguments) {
            if (argument instanceof FlagArgument) {
                containsFlags = true;
                continue;
            }

            if (argument instanceof LimitlessArgument) {
                containsLimitless = true;
            }
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "SimpleSubCommand{" +
                "baseCommand=" + baseCommand +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", alias=" + alias +
                ", isDefault=" + isDefault +
                ", arguments=" + arguments +
                ", requirements=" + requirements +
                ", messageRegistry=" + messageRegistry +
                ", containsLimitlessArgument=" + containsLimitless +
                '}';
    }
}
