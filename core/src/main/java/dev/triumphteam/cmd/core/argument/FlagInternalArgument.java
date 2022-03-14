/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.FlagGroup;
import dev.triumphteam.cmd.core.flag.internal.FlagParser;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Flag argument, a {@link LimitlessInternalArgument} but returns {@link Flags} instead.
 * Which contains a {@link Flags} object and the left over to be passed to another {@link LimitlessInternalArgument}.
 *
 * @param <S> The sender type.
 */
public final class FlagInternalArgument<S> extends LimitlessInternalArgument<S> {
    private final FlagGroup<S> flagGroup;
    private final FlagParser<S> flagParser;

    public FlagInternalArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final FlagGroup<S> flagGroup,
            final int position,
            final boolean isOptional
    ) {
        super(name, description, Flags.class, new EmptySuggestion<>(), position, isOptional);
        this.flagGroup = flagGroup;
        this.flagParser = new FlagParser<>(flagGroup);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link Flags} which contains the flags and leftovers.
     */
    @NotNull
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        return flagParser.parse(sender, value.size() == 1 ? Arrays.asList(value.get(0).split(" ")) : value);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final FlagInternalArgument<?> that = (FlagInternalArgument<?>) o;
        return flagGroup.equals(that.flagGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flagGroup);
    }

    @Override
    public @NotNull String toString() {
        return "FlagArgument{" +
                "flagGroup=" + flagGroup +
                ", super=" + super.toString() + "}";
    }
}
