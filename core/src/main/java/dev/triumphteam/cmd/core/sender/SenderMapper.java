package dev.triumphteam.cmd.core.sender;

import org.jetbrains.annotations.NotNull;

// TODO: 11/17/2021 COMMENTS
@FunctionalInterface
public interface SenderMapper<S, DS> {

    default void validate(@NotNull final Class<?> senderClass) {}

    S map(@NotNull final DS defaultSender);

}
