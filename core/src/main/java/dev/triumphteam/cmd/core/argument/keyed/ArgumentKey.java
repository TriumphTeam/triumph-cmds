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

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Identifier for a list of registered named arguments.
 */
public class ArgumentKey extends StringKey {

    private ArgumentKey(final @NotNull String key) {
        super(key);
    }

    /**
     * Factory method for creating a {@link ArgumentKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link ArgumentKey}.
     */
    @Contract("_ -> new")
    public static @NotNull ArgumentKey of(final @NotNull String key) {
        return new ArgumentKey(key);
    }

    @Override
    public @NotNull String toString() {
        return "ArgumentKey{super=" + super.toString() + "}";
    }
}
