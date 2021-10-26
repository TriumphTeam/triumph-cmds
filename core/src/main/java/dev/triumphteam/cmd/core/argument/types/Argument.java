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
package dev.triumphteam.cmd.core.argument.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Command argument.
 * Which is divided into {@link StringArgument} and {@link LimitlessArgument}.
 *
 * @param <S> The sender type.
 * @param <T> The Argument type.
 */
public abstract class Argument<S, T> {

    private final String name;
    private final Class<?> type;
    private final boolean optional;

    public Argument(
            @NotNull final String name,
            @NotNull final Class<?> type,
            final boolean optional
    ) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    /**
     * Gets the name of the argument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler argument <code>-parameters</code> to show actual names.
     *
     * @return The argument name.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * The argument type.
     * Holds the class type of the argument.
     *
     * @return The argument type.
     */
    @NotNull
    public Class<?> getType() {
        return type;
    }

    /**
     * If argument is optional or not.
     *
     * @return Whether the argument is optional.
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The argument value.
     * @return An object with the resolved value.
     */
    @Nullable
    public abstract Object resolve(@NotNull final S sender, @NotNull final T value);

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Argument<?, ?> argument = (Argument<?, ?>) o;
        return optional == argument.optional && name.equals(argument.name) && type.equals(argument.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, optional);
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isOptional=" + optional +
                '}';
    }

}
