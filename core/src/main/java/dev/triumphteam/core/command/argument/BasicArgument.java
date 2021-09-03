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
package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicArgument<S> implements Argument<S> {

    private final Class<?> type;
    private final ArgumentResolver<S> resolver;
    private final boolean optional;

    public BasicArgument(
            @NotNull final Class<?> type,
            @NotNull final ArgumentResolver<S> resolver,
            final boolean optional
    ) {
        this.type = type;
        this.resolver = resolver;
        this.optional = optional;
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        if (!(value instanceof String)) return null;
        return resolver.resolve(sender, (String) value);
    }

    @Override
    public String toString() {
        return "BasicArgument{" +
                "type=" + type +
                ", resolver=" + resolver +
                ", optional=" + optional +
                '}';
    }
}
