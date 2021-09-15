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
package dev.triumphteam.cmds.core.argument.types;

import dev.triumphteam.cmds.core.argument.ArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Normal {@link StringArgument}.
 * Basically the main implementation.
 * Uses an {@link ArgumentResolver} from the {@link dev.triumphteam.cmds.core.argument.ArgumentRegistry}.
 * Allows you to register many other simple argument types.
 *
 * @param <S> The sender type.
 */
public final class ResolverArgument<S> extends StringArgument<S> {

    private final ArgumentResolver<S> resolver;

    public ResolverArgument(
            @NotNull final String name,
            @NotNull final Class<?> type,
            @NotNull final ArgumentResolver<S> resolver,
            final boolean optional
    ) {
        super(name, type, optional);
        this.resolver = resolver;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The {@link String} argument value.
     * @return An Object value of the correct type, based on the result from the {@link ArgumentResolver}.
     */
    @Nullable
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final String value) {
        return resolver.resolve(sender, value);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final ResolverArgument<?> that = (ResolverArgument<?>) o;
        return resolver.equals(that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resolver);
    }

    @Override
    public String toString() {
        return "ResolverArgument{" +
                "resolver=" + resolver +
                ", super=" + super.toString() + "}";
    }
    
}
