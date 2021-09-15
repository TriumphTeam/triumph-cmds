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
package dev.triumphteam.cmds.core.flag.internal.result;

import dev.triumphteam.cmds.core.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Returned when the parsing is successful.
 *
 * @param <S> The sender type.
 */
public final class SuccessResult<S> implements ParseResult<S> {

    private final List<String> leftOvers;
    private final Flags flags;

    public SuccessResult(
            @NotNull final List<String> leftOvers,
            @NotNull final Flags flags
    ) {
        this.leftOvers = leftOvers;
        this.flags = flags;
    }

    /**
     * Gets a list with the leftover arguments.
     * Which can then be given back to the command.
     *
     * @return The leftovers.
     */
    @NotNull
    public List<String> getLeftOvers() {
        return leftOvers;
    }

    /**
     * Get {@link Flags} instance with the parsed flags.
     *
     * @return The parsed flags.
     */
    @NotNull
    public Flags getFlags() {
        return flags;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SuccessResult<?> that = (SuccessResult<?>) o;
        return leftOvers.equals(that.leftOvers) && flags.equals(that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOvers, flags);
    }

    @Override
    public String toString() {
        return "SuccessResult{" +
                "leftOvers=" + leftOvers +
                ", flags=" + flags +
                '}';
    }
}
