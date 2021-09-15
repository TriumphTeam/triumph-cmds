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
package dev.triumphteam.cmds.core.flag.internal;

import dev.triumphteam.cmds.core.argument.types.StringArgument;
import dev.triumphteam.cmds.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandFlag<S> {

    private final String flag;
    private final String longFlag;

    private String description;

    private final StringArgument<S> argument;
    private final Class<?> argumentType;
    private final boolean optionalArg;
    private final boolean required;

    public CommandFlag(
            @Nullable final String flag,
            @Nullable final String longFlag,
            @Nullable final StringArgument<S> argument,
            final boolean optionalArg,
            final boolean required
    ) {
        this.flag = flag;
        this.longFlag = longFlag;
        this.optionalArg = optionalArg;
        this.required = required;
        this.argument = argument;

        if (argument != null) argumentType = argument.getType();
        else argumentType = null;
    }

    @Nullable
    public String getFlag() {
        return flag;
    }

    @Nullable
    public String getLongFlag() {
        return longFlag;
    }

    @Nullable
    public Class<?> getArgumentType() {
        return argumentType;
    }

    public boolean isRequired() {
        return required;
    }

    public int getId() {
        return getKey().charAt(0);
    }

    @NotNull
    public String getKey() {
        // Will never happen.
        if (flag == null && longFlag == null) {
            throw new CommandExecutionException("Both options can't be null.");
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
        if (argument == null) return null;
        return argument.resolve(sender, token);
    }

}
