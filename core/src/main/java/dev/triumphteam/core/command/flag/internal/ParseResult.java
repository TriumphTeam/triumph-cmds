/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.command.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class ParseResult {

    private final List<String> leftOvers;
    private final Flags flags;

    public ParseResult(@NotNull final List<String> leftOvers, @Nullable final Flags flags) {
        this.leftOvers = leftOvers;
        this.flags = flags;
    }

    @NotNull
    public List<String> getLeftOvers() {
        return leftOvers;
    }

    @Nullable
    public Flags getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "leftOvers=" + leftOvers +
                ", flags=" + flags +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ParseResult that = (ParseResult) o;
        return Objects.equals(leftOvers, that.leftOvers) && Objects.equals(flags, that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOvers, flags);
    }

}
