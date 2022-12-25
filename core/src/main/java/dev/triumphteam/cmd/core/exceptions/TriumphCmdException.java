package dev.triumphteam.cmd.core.exceptions;

import org.jetbrains.annotations.NotNull;

public final class TriumphCmdException extends RuntimeException {

    public TriumphCmdException(final @NotNull String message) {
        super(message);
    }
}
