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

import dev.triumphteam.cmd.core.extention.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Normal {@link StringInternalArgument}.
 * Basically the main implementation.
 * Uses an {@link ArgumentResolver} from the {@link ArgumentRegistry}.
 * Allows you to register many other simple argument types.
 *
 * @param <S> The sender type.
 */
public final class ResolverInternalArgument<S> extends StringInternalArgument<S> {

    private final ArgumentResolver<S> resolver;

    public ResolverInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull ArgumentResolver<S> resolver,
            final @NotNull Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(name, description, type, suggestion, optional);
        this.resolver = resolver;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The {@link String} argument value.
     * @return An Object value of the correct type, based on the result from the {@link ArgumentResolver}.
     */
    @Override
    public @Nullable Object resolve(final @NotNull S sender, final @NotNull String value) {
        return resolver.resolve(sender, value);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final ResolverInternalArgument<?> that = (ResolverInternalArgument<?>) o;
        return resolver.equals(that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resolver);
    }

    @Override
    public @NotNull String toString() {
        return "ResolverArgument{" +
                "resolver=" + resolver +
                ", super=" + super.toString() + "}";
    }

}
