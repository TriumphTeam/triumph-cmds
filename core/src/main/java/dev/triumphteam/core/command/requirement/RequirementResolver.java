package dev.triumphteam.core.command.requirement;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RequirementResolver<S> {

    boolean resolve(@NotNull final S sender);

}
