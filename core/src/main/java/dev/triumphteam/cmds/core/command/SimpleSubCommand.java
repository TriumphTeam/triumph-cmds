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
package dev.triumphteam.cmds.core.command;

import dev.triumphteam.cmds.core.BaseCommand;
import dev.triumphteam.cmds.core.command.argument.types.Argument;
import dev.triumphteam.cmds.core.command.argument.types.FlagArgument;
import dev.triumphteam.cmds.core.command.argument.types.LimitlessArgument;
import dev.triumphteam.cmds.core.command.argument.types.StringArgument;
import dev.triumphteam.cmds.core.command.flag.Flags;
import dev.triumphteam.cmds.core.command.flag.internal.ParseResult;
import dev.triumphteam.cmds.core.command.message.MessageKey;
import dev.triumphteam.cmds.core.command.message.MessageRegistry;
import dev.triumphteam.cmds.core.command.message.context.DefaultMessageContext;
import dev.triumphteam.cmds.core.command.message.context.InvalidArgumentContext;
import dev.triumphteam.cmds.core.command.requirement.RequirementResolver;
import dev.triumphteam.cmds.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class SimpleSubCommand<S> implements SubCommand<S> {

    private final BaseCommand baseCommand;
    private final Method method;

    private final String name;
    private final List<String> alias;
    private final boolean isDefault;
    private final int priority;

    private final List<Argument<S, ?>> arguments;
    private final Set<RequirementResolver<S>> requirements;

    private final MessageRegistry<S> messageRegistry;

    private boolean containsLimitless = false;
    private boolean containsFlags = false;

    public SimpleSubCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final List<Argument<S, ?>> arguments,
            @NotNull final Set<RequirementResolver<S>> requirements,
            @NotNull final MessageRegistry<S> messageRegistry,
            final boolean isDefault,
            final int priority
    ) {
        this.baseCommand = baseCommand;
        this.method = method;
        this.name = name;
        this.alias = alias;
        this.arguments = arguments;
        this.requirements = requirements;
        this.messageRegistry = messageRegistry;
        this.isDefault = isDefault;
        this.priority = priority;

        checkArguments();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getAlias() {
        return alias;
    }

    @NotNull
    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public List<Argument<S, ?>> getArguments() {
        return arguments;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        // Removes the sub command from the args if it's not default.
        final List<String> commandArgs = getCommandArgs(args);

        // Creates the invoking arguments list
        final List<Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        if (!validateAndCollectArguments(sender, invokeArguments, commandArgs)) {
            return;
        }

        if ((!containsLimitless && !containsFlags) && commandArgs.size() >= invokeArguments.size()) {
            messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new DefaultMessageContext(commandArgs));
            return;
        }

        try {
            method.invoke(baseCommand, invokeArguments.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

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
                throw new CommandExecutionException("Found unsupported argument.");
            }

            final StringArgument<S> stringArgument = (StringArgument<S>) argument;
            final String arg = valueOrNull(commandArgs, i);

            if (arg == null) {
                if (argument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }

                messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new DefaultMessageContext(commandArgs));
                return false;
            }

            final Object result = stringArgument.resolve(sender, arg);
            if (result == null) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        new InvalidArgumentContext(commandArgs, arg, argument.getName(), argument.getType())
                );
                return false;
            }

            invokeArguments.add(result);
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

        final ParseResult result;
        if (containsLimitless) {
            //noinspection unchecked
            final LimitlessArgument<S> tempArg = (LimitlessArgument<S>) arguments.get(index + 1);
            result = getFlagResult(tempArg, sender, args);
        } else {
            result = getFlagResult(argument, sender, args);
        }

        final Flags flags = result.getFlags();
        if (flags == null) {
            messageRegistry.sendMessage(MessageKey.MISSING_FLAG, sender, new DefaultMessageContext(args));
            return false;
        }

        if (containsLimitless) {
            invokeArguments.add(argument.resolve(sender, result.getLeftOvers()));
        }

        invokeArguments.add(flags);
        return true;
    }

    private ParseResult getFlagResult(
            @NotNull final LimitlessArgument<S> argument,
            @NotNull final S sender,
            @NotNull final List<String> args
    ) {
        if (!(argument instanceof FlagArgument)) {
            throw new CommandExecutionException("An error occurred while handling command flags.");
        }
        final FlagArgument<S> flagArgument = (FlagArgument<S>) argument;
        return flagArgument.resolve(sender, args);
    }

    @NotNull
    private List<String> getCommandArgs(@NotNull final List<String> args) {
        if (!isDefault) {
            return args.subList(1, args.size());
        }

        return args;
    }

    @Nullable
    private String valueOrNull(@NotNull final List<String> list, final int index) {
        if (index >= list.size()) return null;
        return list.get(index);
    }

    @NotNull
    private List<String> leftOvers(@NotNull final List<String> list, final int from) {
        if (from > list.size()) return Collections.emptyList();
        return list.subList(from, list.size());
    }

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
    @Contract(pure = true)
    public String toString() {
        return "SimpleSubCommand{" +
                "baseCommand=" + baseCommand +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", alias=" + alias +
                ", isDefault=" + isDefault +
                ", priority=" + priority +
                ", arguments=" + arguments +
                ", requirements=" + requirements +
                ", messageRegistry=" + messageRegistry +
                ", containsLimitlessArgument=" + containsLimitless +
                '}';
    }
}
