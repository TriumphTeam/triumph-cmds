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

import dev.triumphteam.cmds.core.command.argument.Argument;
import dev.triumphteam.cmds.core.command.argument.LimitlessArgument;
import dev.triumphteam.cmds.core.command.argument.StringArgument;
import dev.triumphteam.cmds.core.command.flag.internal.FlagGroup;
import dev.triumphteam.cmds.core.command.requirement.RequirementResolver;
import dev.triumphteam.cmds.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private final FlagGroup<S> flagGroup;
    private final Set<RequirementResolver<S>> requirements;

    private final boolean containsLimitlessArgument;

    public SimpleSubCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final List<Argument<S, ?>> arguments,
            @NotNull final FlagGroup<S> flagGroup,
            @NotNull final Set<RequirementResolver<S>> requirements,
            final boolean isDefault,
            final int priority
    ) {
        this.baseCommand = baseCommand;
        this.method = method;
        this.name = name;
        this.alias = alias;
        this.arguments = arguments;
        this.flagGroup = flagGroup;
        this.requirements = requirements;
        this.isDefault = isDefault;
        this.priority = priority;

        this.containsLimitlessArgument = containsLimitless();
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

    @SuppressWarnings("unchecked")
    @Override
    public CommandExecutionResult execute(@NotNull final S sender, @NotNull final List<String> args) {
        // Removes the sub command from the args if it's not default.
        final List<String> commandArgs;
        if (!isDefault) {
            commandArgs = args.subList(1, args.size());
        } else {
            commandArgs = args;
        }

        if (isDefault && arguments.isEmpty() && !commandArgs.isEmpty()) {
            // TODO error
            //System.out.println("hurr durr wrong usage");
            return CommandExecutionResult.WRONG_USAGE;
        }

        final List<Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        for (int i = 0; i < arguments.size(); i++) {
            final Argument<S, ?> argument = arguments.get(i);

            if (argument instanceof LimitlessArgument) {
                final LimitlessArgument<S> limitlessArgument = (LimitlessArgument<S>) argument;
                final List<String> leftOvers = leftOversOrNull(commandArgs, i);

                if (leftOvers == null) {
                    if (argument.isOptional()) {
                        invokeArguments.add(null);
                        continue;
                    }

                    return CommandExecutionResult.WRONG_USAGE;
                }

                final Object result = limitlessArgument.resolve(sender, leftOvers);
                if (result == null) {
                    // TODO error

                    //System.out.println("hurr durr invalid arg");
                    return CommandExecutionResult.WRONG_USAGE;
                }

                invokeArguments.add(result);
                continue;
            }

            // TODO Should never happen, might need to handle it better
            if (!(argument instanceof StringArgument)) {
                break;
            }

            final StringArgument<S> stringArgument = (StringArgument<S>) argument;
            final String arg = valueOrNull(commandArgs, i);

            if (arg == null) {
                if (argument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }

                // TODO error
                //System.out.println("hurr durr not enoug args");
                return CommandExecutionResult.WRONG_USAGE;
            }

            final Object result = stringArgument.resolve(sender, arg);
            if (result == null) {
                // TODO error
                System.out.println(arg);
                //System.out.println("hurr durr invalid arg");
                return CommandExecutionResult.WRONG_USAGE;
            }

            invokeArguments.add(result);
        }

        if (!containsLimitlessArgument && commandArgs.size() >= invokeArguments.size()) {
            // TODO error
            //System.out.println("hurr durr too many args");
            return CommandExecutionResult.WRONG_USAGE;
        }

        try {
            method.invoke(baseCommand, invokeArguments.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return CommandExecutionResult.SUCCESS;
    }

    @Nullable
    private String valueOrNull(@NotNull final List<String> list, final int index) {
        if (index >= list.size()) return null;
        return list.get(index);
    }

    @Nullable
    private List<String> leftOversOrNull(@NotNull final List<String> list, final int from) {
        if (from >= list.size()) return null;
        return list.subList(from, list.size());
    }

    private boolean containsLimitless() {
        for (final Argument<S, ?> argument : arguments) {
            if (argument instanceof LimitlessArgument) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "SimpleSubCommand{" +
                "baseCommand=" + baseCommand +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
