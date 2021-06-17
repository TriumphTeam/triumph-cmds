package dev.triumphteam.core.exceptions;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class SubCommandRegistrationException extends RuntimeException {

    public SubCommandRegistrationException(
            @NotNull final String message,
            @NotNull final Method method
    ) {
        super(message + " On Method: `" + method.getName() + "`");
    }

    public SubCommandRegistrationException(@NotNull final String message) {
        super(message);
    }

}
