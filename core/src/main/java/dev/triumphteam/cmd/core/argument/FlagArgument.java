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

import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.FlagGroup;
import dev.triumphteam.cmd.core.flag.internal.FlagParser;
import dev.triumphteam.cmd.core.flag.internal.result.ParseResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Flag argument, a {@link LimitlessArgument} but returns a {@link ParseResult} instead.
 * Which contains a {@link Flags} object and the left over to be passed to another {@link LimitlessArgument}.
 *
 * @param <S> The sender type.
 */
public final class FlagArgument<S> extends LimitlessArgument<S> {

    private final FlagGroup<S> flagGroup;

    public FlagArgument(
            @NotNull final FlagGroup<S> flagGroup,
            @NotNull final String name,
            final boolean isOptional
    ) {
        super(name, Flags.class, isOptional);
        this.flagGroup = flagGroup;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link ParseResult} which contains the flags and left overs.
     */
    @NotNull
    @Override
    public ParseResult<S> resolve(@NotNull final S sender, @NotNull final List<String> value) {
        final List<String> args = value.size() == 1 ? Arrays.asList(value.get(0).split(" ")) : value;
        System.out.println(args);
        return FlagParser.parse(flagGroup, sender, args);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final FlagArgument<?> that = (FlagArgument<?>) o;
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
