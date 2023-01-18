/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.argument.keyed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

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
     * @param flags The {@link List} of {@link Flag}s.
     * @return A {@link FlagGroup} instance.
     */
    static ArgumentGroup<Flag> flags(final @NotNull List<Flag> flags) {
        return new FlagGroup(flags);
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
     * Gets the argument that matches the current token.
     *
     * @param token The current token, an argument name or not.
     * @return The argument if found or null if not a valid argument name.
     */
    @Nullable T getMatchingArgument(final @NotNull String token);

    /**
     * Gets a list with all possible argument names.
     *
     * @return A {@link Set} of names.
     */
    @NotNull Set<String> getAllNames();

    /**
     * @return Whether the group is empty.
     */
    boolean isEmpty();

    /**
     * @return Gets a set with all the arguments of type {@link T}.
     */
    @NotNull Set<T> getAll();
}
