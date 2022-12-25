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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Joined string argument, a {@link LimitlessInternalArgument}.
 * Returns a single {@link String} that was joined from a {@link List} of arguments.
 *
 * @param <S> The sender type.
 */
public final class JoinedStringInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final CharSequence delimiter;

    public JoinedStringInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull CharSequence delimiter,
            final @NotNull Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(name, description, String.class, suggestion, optional);
        this.delimiter = delimiter;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A single {@link String} with the joined {@link List}.
     */
    @Override
    public @NotNull Object resolve(final @NotNull S sender, final @NotNull List<@NotNull String> value) {
        return String.join(delimiter, value);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final JoinedStringInternalArgument<?> that = (JoinedStringInternalArgument<?>) o;
        return delimiter.equals(that.delimiter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delimiter);
    }

    @Override
    public @NotNull String toString() {
        return "JoinedStringArgument{" +
                "delimiter=" + delimiter +
                ", super=" + super.toString() + "}";
    }

}
