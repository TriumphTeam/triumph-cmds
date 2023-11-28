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

import java.util.Optional;
import java.util.Set;

/**
 * Contains all the command flags that was typed by the user in an easy-to-access way.
 */
public interface Flags extends Keyed {

    /**
     * Checks if the flag key is present or not.
     * Useful for simple flags like <code>-l</code>.
     * Where you just want to check if the flag was added or not.
     * For flag with values recommended {@link Flags#getFlagValue(String, Class)}.
     *
     * @param flag The flag to check.
     * @return Whether the flag is present in the command or not.
     */
    boolean hasFlag(final @NotNull String flag);

    /**
     * Gets the flag value.
     * If the value is not present OR if the value is not the correct type, optional will be empty.
     *
     * @param flag The flag to get the value from.
     * @param type The {@link Class} of the value to get.
     * @param <T>  The value type, based on the class from before.
     * @return The flag's value.
     */
    <T> @NotNull Optional<T> getFlagValue(final @NotNull String flag, final @NotNull Class<T> type);

    /**
     * Instead of converting the value to the desired type, simply get it as string.
     * If flag is not present then optional will be empty.
     *
     * @param flag The flag to get the value from.
     * @return The flag's value.
     */
    @NotNull Optional<String> getFlagValue(final @NotNull String flag);

    /**
     * @return A {@link Set} with the present Flags.
     */
    @NotNull Set<String> getAllFlags();

    /**
     * Check if flags were typed.
     *
     * @return true if any flag was typed in the command.
     */
    boolean hasFlags();
}
