package dev.triumphteam.core.command.message;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MessageResolver<S> {

    void resolve(@NotNull final S sender);

}
