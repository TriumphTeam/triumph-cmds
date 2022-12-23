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
package dev.triumphteam.cmd.core.flag.internal;

import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ArgFlagValue<S> implements FlagValue {

    private final String value;
    private final StringInternalArgument<S> argument;

    public ArgFlagValue(final @NotNull String value, final @NotNull StringInternalArgument<S> argument) {
        this.value = value;
        this.argument = argument;
    }

    public @Nullable Object getValue(final @NotNull S sender, final @NotNull Class<?> type) {
        if (!type.equals(argument.getType())) return null;
        return argument.resolve(sender, value);
    }

    public @NotNull String getAsString() {
        return value;
    }

    @Override
    public @NotNull String toString() {
        return "ArgFlagValue{" +
                "value='" + value + '\'' +
                ", argument=" + argument +
                '}';
    }
}
