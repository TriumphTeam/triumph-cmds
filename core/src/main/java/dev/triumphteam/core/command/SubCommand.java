package dev.triumphteam.core.command;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.argument.Argument;
import dev.triumphteam.core.command.argument.JoinableStringArgument;
import dev.triumphteam.core.command.argument.LimitlessArgument;
import dev.triumphteam.core.command.requirement.RequirementResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class SubCommand<S> {

    private final BaseCommand baseCommand;
    private final Method method;

    private final String name;
    private final List<String> alias;
    private final boolean isDefault;

    private final List<Argument<S>> arguments;
    private final Set<RequirementResolver<S>> requirements;

    private final boolean containsLimitlessArgument;

    public SubCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final List<Argument<S>> arguments,
            @NotNull final Set<RequirementResolver<S>> requirements,
            final boolean isDefault
    ) {
        this.baseCommand = baseCommand;
        this.method = method;
        this.name = name;
        this.alias = alias;
        this.arguments = arguments;
        this.requirements = requirements;
        this.isDefault = isDefault;

        this.containsLimitlessArgument = checkArguments();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getAlias() {
        return alias;
    }

    public void execute(@NotNull S sender, @NotNull final List<String> args) {
        // Removes the sub command from the args if it's not default.
        if (!isDefault) {
            args.remove(0);
        }

        if (isDefault && arguments.isEmpty() && !args.isEmpty()) {
            // TODO error
            System.out.println("hurr durr wrong usage");
            return;
        }

        final List<Object> invokeArguments = new LinkedList<>();
        invokeArguments.add(sender);

        for (int i = 0; i < arguments.size(); i++) {
            final Argument<S> argument = arguments.get(i);

            final Object arg;
            if (argument instanceof JoinableStringArgument) arg = leftOversOrNull(args, i);
            else arg = valueOrNull(args, i);

            if (arg == null) {
                if (argument.isOptional()) {
                    invokeArguments.add(null);
                    continue;
                }
                // TODO error
                System.out.println("hurr durr not enoug args");
                return;
            }

            final Object result = argument.resolve(sender, arg);
            if (result == null) {
                // TODO error
                System.out.println("hurr durr invalid arg");
                return;
            }

            invokeArguments.add(result);
        }

        if (!containsLimitlessArgument && args.size() >= invokeArguments.size()) {
            // TODO error
            System.out.println("hurr durr too many args");
            return;
        }

        try {
            System.out.println("Executed correctly");
            method.invoke(baseCommand, invokeArguments.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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

    private boolean checkArguments() {
        for (final Argument<S> argument : arguments) {
            if (argument instanceof LimitlessArgument) return true;
        }

        return false;
    }

}
