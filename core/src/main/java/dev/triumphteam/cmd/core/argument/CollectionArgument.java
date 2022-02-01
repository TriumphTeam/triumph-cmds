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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collection argument, a {@link LimitlessArgument} but returns a {@link List} instead.
 * Currently, only supports {@link List} and {@link Set}.
 *
 * @param <S> The sender type.
 */
public final class CollectionArgument<S> extends LimitlessArgument<S> {

    private final Argument<S, String> argument;
    private final Class<?> collectionType;

    public CollectionArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Argument<S, String> argument,
            @NotNull final Class<?> collectionType,
            final int position,
            final boolean optional
    ) {
        super(name, description, String.class, position, optional);
        this.argument = argument;
        this.collectionType = collectionType;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link java.util.Collection} type as the resolved value.
     */
    @NotNull
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        final Stream<Object> stream = value.stream().map(arg -> argument.resolve(sender, arg));
        if (collectionType == Set.class) return stream.collect(Collectors.toSet());
        return stream.collect(Collectors.toList());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final CollectionArgument<?> that = (CollectionArgument<?>) o;
        return collectionType.equals(that.collectionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), collectionType);
    }

    @Override
    public @NotNull String toString() {
        return "CollectionArgument{" +
                "collectionType=" + collectionType +
                ", super=" + super.toString() + "}";
    }
}
