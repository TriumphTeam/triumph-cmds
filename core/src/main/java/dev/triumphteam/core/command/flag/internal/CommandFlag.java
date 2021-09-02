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
package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.command.argument.ArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandFlag<S> {

    private final String flag;
    private final String longFlag;

    private String description;

    private final Class<?> argument;
    private final boolean optionalArg;
    private final boolean required;

    private final ArgumentResolver<S> argumentResolver;

    public CommandFlag(
            @Nullable final String flag,
            @Nullable final String longFlag,
            @Nullable final Class<?> argument,
            final boolean optionalArg,
            final boolean required,
            @Nullable final ArgumentResolver<S> argumentResolver
    ) {
        this.flag = flag;
        this.longFlag = longFlag;
        this.argument = argument;
        this.optionalArg = optionalArg;
        this.required = required;
        this.argumentResolver = argumentResolver;
    }

    @Nullable
    public String getFlag() {
        return flag;
    }

    @Nullable
    public String getLongFlag() {
        return longFlag;
    }

    public boolean isRequired() {
        return required;
    }

    public int getId() {
        return getKey().charAt(0);
    }

    @NotNull
    String getKey() {
        // Will never happen.
        if (flag == null && longFlag == null) {
            throw new IllegalArgumentException("Both options can't be null.");
        }

        return (flag == null) ? longFlag : flag;
    }

    boolean hasArgument() {
        return argument != null;
    }

    boolean requiresArg() {
        return !optionalArg;
    }

    @Nullable
    public Object resolveArgument(@NotNull S sender, @NotNull final String token) {
        return argumentResolver.resolve(sender, token);
    }

}
