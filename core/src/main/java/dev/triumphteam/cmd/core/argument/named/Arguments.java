package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Arguments {

    /**
     * Gets an argument by name.
     * The argument will be an empty {@link Optional} if it does not exist or if the value is invalid.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    @NotNull <T> Optional<T> get(@NotNull final String name, @NotNull final Class<T> type);


    @NotNull <T> Optional<List<T>> getAsList(@NotNull final String name, @NotNull final Class<T> type);

    @NotNull <T> Optional<Set<T>> getAsSet(@NotNull final String name, @NotNull final Class<T> type);
}
