package dev.triumphteam.core.exceptions;

import dev.triumphteam.core.internal.BaseCommand;
import org.jetbrains.annotations.NotNull;

public final class CommandRegistrationException extends RuntimeException {

    public CommandRegistrationException(
            @NotNull final String message,
            @NotNull final Class<? extends BaseCommand> commandClass
    ) {
        super(message + " on Class `" + commandClass.getName() + "`");
    }

}
