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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Arguments extends Flags {

    /**
     * Gets an argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NotNull Optional<T> getArgument(final @NotNull String name, final @NotNull Class<T> type);

    /**
     * Gets a {@link List} argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NotNull Optional<List<T>> getListArgument(final @NotNull String name, final @NotNull Class<T> type);

    /**
     * Gets a {@link Set} argument by name.
     * The argument will be an empty {@link Optional} if it does not exist.
     *
     * @param name The name of the argument.
     * @param type The class of the type of the argument.
     * @param <T>  The generic type of the argument.
     * @return An {@link Optional} argument.
     */
    <T> @NotNull Optional<Set<T>> getSetArgument(final @NotNull String name, final @NotNull Class<T> type);

    /**
     * Get all arguments passed to this command.
     *
     * @return a {@link Map} of all arguments.
     */
    @NotNull Map<String, Object> getAllArguments();

    /**
     * Check if arguments were typed.
     *
     * @return true if any argument was typed in the command.
     */
    boolean hasArguments();
}
