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

import dev.triumphteam.cmd.core.command.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains all the "settings" for the flag.
 *
 * @param <S> The sender type.
 */
public final class FlagOptions<S> {

    private final String flag;
    private final String longFlag;

    // TODO: 9/16/2021 Check if flag description is needed.
    private final StringInternalArgument<S> argument;

    public FlagOptions(
            final @Nullable String flag,
            final @Nullable String longFlag,
            final @Nullable StringInternalArgument<S> argument
    ) {
        this.flag = flag;
        this.longFlag = longFlag;
        this.argument = argument;
    }

    /**
     * Gets the flag identifier.
     *
     * @return The flag identifier.
     */
    public @Nullable String getFlag() {
        return flag;
    }

    /**
     * Gets the long flag identifier.
     *
     * @return The long flag identifier.
     */
    public @Nullable String getLongFlag() {
        return longFlag;
    }

    // TODO: Comments
    public @Nullable StringInternalArgument<S> getArgument() {
        return argument;
    }

    /**
     * They key will either be the {@link FlagOptions#getFlag()} or the {@link FlagOptions#getLongFlag()}.
     *
     * @return The key that identifies the flag.
     */
    public @NotNull String getKey() {
        // Will never happen.
        if (flag == null && longFlag == null) {
            throw new CommandExecutionException("Both options can't be null.");
        }

        return (flag == null) ? longFlag : flag;
    }

    /**
     * Checks if the flag has argument or not.
     *
     * @return Whether it has an argument.
     */
    public boolean hasArgument() {
        return argument != null;
    }
}
