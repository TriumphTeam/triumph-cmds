package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Arguments {

    /**
     * Gets an argument by name.
     * The argument will be an empty {@link Optional} if it does not exist or if the value is invalid.
     * TODO: Validate value in the future.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    @NotNull <T> Optional<T> get(@NotNull final String name, @NotNull final Class<T> type);

}
