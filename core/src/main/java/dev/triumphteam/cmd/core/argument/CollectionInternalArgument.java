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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collection argument, a {@link LimitlessInternalArgument} but returns a {@link List} instead.
 * Currently, only supports {@link List} and {@link Set}.
 *
 * @param <S> The sender type.
 */
public final class CollectionInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final InternalArgument<S, String> internalArgument;
    private final Class<?> collectionType;

    public CollectionInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull InternalArgument<S, String> internalArgument,
            final @NotNull Class<?> collectionType,
            final @NotNull Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(name, description, String.class, suggestion, optional);
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link java.util.Collection} type as the resolved value.
     */
    @Override
    public @NotNull Object resolve(final @NotNull S sender, final @NotNull List<@NotNull String> value) {
        final Stream<Object> stream = value.stream().map(arg -> internalArgument.resolve(sender, arg));
        if (collectionType == Set.class) return stream.collect(Collectors.toSet());
        return stream.collect(Collectors.toList());
    }

    @Override
    public @NotNull String toString() {
        return "CollectionArgument{" +
                "collectionType=" + collectionType +
                ", super=" + super.toString() + "}";
    }
}
