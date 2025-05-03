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

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection argument, a {@link LimitlessInternalArgument} but returns a {@link List} instead.
 * Currently, only supports {@link List} and {@link Set}.
 *
 * @param <S> The sender type.
 */
public final class CollectionInternalArgument<S, ST> extends LimitlessInternalArgument<S, ST> {

    private final InternalArgument<S, ST> internalArgument;
    private final Class<?> collectionType;

    public CollectionInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull InternalArgument<S, ST> internalArgument,
            final @NotNull Class<?> collectionType,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, defaultValue, optional);
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @return A {@link java.util.Collection} type as the resolved value.
     */
    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input
    ) {
        return resolveCollection(sender, internalArgument, Arrays.asList(input.getInput().split(" ")), collectionType);
    }

    public static <S, ST> @NotNull InternalArgumentResult resolveCollection(
            final @NotNull S sender,
            final @NotNull InternalArgument<S, ST> internalArgument,
            final @NotNull Collection<String> value,
            final @NotNull Class<?> collectionType
    ) {
        // Create a collection based on the type.
        final Collection<Object> collection = createCollection(collectionType);

        for (final String arg : value) {
            final InternalArgumentResult resolved = internalArgument.resolve(sender, new ArgumentInput(arg));

            // If an error occurs, it needs to be delegated back to the caller.
            if (!(resolved instanceof InternalArgumentResult.Valid)) return resolved;

            // If success, then we collect the result's value.
            collection.add(((InternalArgumentResult.Valid) resolved).getValue());
        }

        // Return the collection as a success
        return InternalArgument.valid(collection);
    }

    private static Collection<Object> createCollection(final Class<?> collectionType) {
        if (collectionType == Set.class) return new HashSet<>();
        return new ArrayList<>();
    }

    @Override
    public @NotNull String toString() {
        return "CollectionArgument{" +
                "collectionType=" + collectionType +
                ", super=" + super.toString() + "}";
    }
}
