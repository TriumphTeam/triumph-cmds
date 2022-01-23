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
package dev.triumphteam.cmd.core.flag;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Contains all the command flags that was typed by the user in an easy-to-access way.
 */
public interface Flags {

    /**
     * Checks if the flag key is present or not.
     * Useful for simple flags like <code>-l</code>.
     * Where you just want to check if the flag was added or not.
     * For flag with values recommended {@link Flags#getValueOrNull(String, Class)}.
     *
     * @param flag The flag to check.
     * @return Whether the flag is present in the command or not.
     */
    boolean hasFlag(@NotNull final String flag);

    /**
     * Gets the flag value in a not nullable way.
     * However, it'll throw exception if the flag isn't present.
     * Recommended use for required flags with required argument.
     *
     * @param flag The flag to get the value from.
     * @param type The {@link Class} of the value to get.
     * @param <T>  The value type, based on the class from before.
     * @return The flag's value.
     * @throws CommandExecutionException Thrown in case the flag doesn't exist.
     */
    @NotNull <T> T getValue(@NotNull final String flag, @NotNull final Class<T> type);

    /**
     * Gets the flag value in a nullable way.
     *
     * @param flag The flag to get the value from.
     * @param type The {@link Class} of the value to get.
     * @param <T>  The value type, based on the class from before.
     * @return The flag's value or null.
     */
    @Nullable <T> T getValueOrNull(@NotNull final String flag, @NotNull final Class<T> type);

    /**
     * Gets the flag value in a not nullable way since a default value will be given.
     *
     * @param flag The flag to get the value from.
     * @param type The {@link Class} of the value to get.
     * @param <T>  The value type, based on the class from before.
     * @return The flag's value.
     */
    @NotNull <T> T getValueOrDefault(@NotNull final String flag, @NotNull final Class<T> type, @NotNull final T def);

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @return The arguments joined to string.
     */
    @NotNull
    String getText();

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @param delimiter The delimiter of the joining.
     * @return The arguments joined to string with a delimiter.
     */
    @NotNull
    String getText(@NotNull final String delimiter);

    /**
     * Gets the arguments typed without the flags.
     *
     * @return A {@link List} with the typed arguments.
     */
    @NotNull
    List<String> getArgs();

}
