package dev.triumphteam.core.exceptions;

import dev.triumphteam.core.internal.CommandBase;
import org.jetbrains.annotations.NotNull;

public final class CommandRegistrationException extends RuntimeException {

    public CommandRegistrationException(
            @NotNull final String message,
            @NotNull final Class<? extends CommandBase> commandClass
    ) {
        super(message + " on Class `" + commandClass.getName() + "`");
    }

}
