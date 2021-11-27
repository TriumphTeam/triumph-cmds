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
package dev.triumphteam.cmd.core.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Pair is an util for creating a value with 2 types.
 *
 * @param <F> The first key type.
 * @param <S> The second key type.
 */
public final class Pair<F, S> {

    private final F first;
    private final S second;

    private Pair(@NotNull final F first, @NotNull final S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new {@link Pair} from the given keys.
     *
     * @param first  The first key value.
     * @param second The second key value.
     * @param <F>    The type of the first key.
     * @param <S>    The type of the second key.
     * @return A new instance of {@link Pair} holding both keys.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static <F, S> Pair<F, S> of(@NotNull final F first, @NotNull final S second) {
        return new Pair<>(first, second);
    }

    /**
     * Gets the first key.
     *
     * @return The first key.
     */
    public F getFirst() {
        return first;
    }

    /**
     * Gets the second key.
     *
     * @return The second key.
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
