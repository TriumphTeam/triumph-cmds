package dev.triumphteam.core.command;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.requirement.RequirementResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public abstract class SubCommand<S> {

    private final BaseCommand baseCommand;
    private final Method method;

    private final String name;
    private final List<String> alias;
    private final boolean isDefault;

    private final Set<RequirementResolver<S>> requirements;

    public SubCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final Set<RequirementResolver<S>> requirements,
            final boolean isDefault
    ) {
        this.baseCommand = baseCommand;
        this.method = method;
        this.name = name;
        this.alias = alias;
        this.requirements = requirements;
        this.isDefault = isDefault;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getAlias() {
        return alias;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void execute(@NotNull S sender, @NotNull final List<String> args) {
        try {

            for (final RequirementResolver<S> requirement : requirements) {
                if (!requirement.resolve(sender)) return;
            }

            method.invoke(baseCommand, sender, 5, "message");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
