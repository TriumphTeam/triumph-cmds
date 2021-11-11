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
package dev.triumphteam.cmd.core.flag.internal;

import dev.triumphteam.cmd.core.flag.Flags;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Simple holder for the flag values.
 * Allows to reduce a bit the checks and having only one Map in the {@link Flags}.
 */
class FlagValue {

    private final Object value;
    private final Class<?> type;

    public FlagValue(@Nullable final Object value, @Nullable final Class<?> type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Gets the flag value.
     *
     * @return The flag value.
     */
    @Nullable
    public Object getValue() {
        return value;
    }

    /**
     * Gets the flag type.
     *
     * @return The flag type.
     */
    @Nullable
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FlagValue flagValue = (FlagValue) o;
        return Objects.equals(value, flagValue.value) && Objects.equals(type, flagValue.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "FlagValue{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }
}
