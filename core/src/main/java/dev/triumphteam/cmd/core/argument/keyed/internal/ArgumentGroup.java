package dev.triumphteam.cmd.core.argument.keyed.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A group of argument data.
 * Example implementations are, flags and named arguments.
 *
 * @param <T> The type of argument of the group.
 */
public interface ArgumentGroup<T> {

    /**
     * Static factory for creating a new flag {@link ArgumentGroup} of type {@link Flag}.
     *
     * @return A {@link FlagGroup} instance.
     */
    static ArgumentGroup<Flag> flags() {
        return new FlagGroup<>();
    }

    /**
     * Static factory for creating a new arguments {@link ArgumentGroup} of type {@link Argument}.
     *
     * @param arguments The {@link List} of {@link Argument}s.
     * @return A {@link NamedGroup} instance.
     */
    static ArgumentGroup<Argument> named(final @NotNull List<Argument> arguments) {
        return new NamedGroup(arguments);
    }

    /**
     * Adds a new argument to the group.
     *
     * @param argument The argument {@link T} that should be added to the group.
     */
    void addArgument(final @NotNull T argument);

    /**
     * Gets the argument that matches the current token.
     *
     * @param token The current token, an argument name or not.
     * @return The argument if found or null if not a valid argument name.
     */
    @Nullable T getMatchingArgument(final @NotNull String token);

    /**
     * Gets a list with all possible argument names.
     *
     * @return A {@link List} of names.
     */
    @NotNull List<String> getAllNames();

    /**
     * Checks if the group is empty.
     *
     * @return Whether the group is empty.
     */
    boolean isEmpty();
}
