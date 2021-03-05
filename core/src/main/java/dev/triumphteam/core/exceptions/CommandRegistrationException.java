package dev.triumphteam.core.exceptions;

import org.jetbrains.annotations.NotNull;

public final class CommandRegistrationException extends RuntimeException {

    public CommandRegistrationException(@NotNull final String message) {
        super(message);
    }

}
