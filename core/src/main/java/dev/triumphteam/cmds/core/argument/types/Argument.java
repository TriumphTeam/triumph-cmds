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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Argument<S, T> {

    private final String name;
    private final Class<?> type;
    private final boolean isOptional;

    public Argument(
            @NotNull final String name,
            @NotNull final Class<?> type,
            final boolean isOptional
    ) {
        this.name = name;
        this.type = type;
        this.isOptional = isOptional;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Class<?> getType() {
        return type;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @Nullable
    public abstract Object resolve(@NotNull S sender, @NotNull final T value);

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isOptional=" + isOptional +
                '}';
    }
}
