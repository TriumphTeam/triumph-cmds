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
package dev.triumphteam.cmds.core.message.context;

import dev.triumphteam.cmds.core.flag.internal.result.InvalidFlagArgumentResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Context for when user types an invalid flag argument based on its type.
 */
public final class InvalidFlagArgumentContext extends AbstractMessageContext {

    private final InvalidFlagArgumentResult<?> result;

    public InvalidFlagArgumentContext(
            @NotNull final String command,
            @NotNull final String subCommand,
            @NotNull final InvalidFlagArgumentResult<?> result
    ) {
        super(command, subCommand);
        this.result = result;
    }

    /**
     * Gets the flag with the invalid argument.
     *
     * @return The flag name.
     */
    @NotNull
    public String getFlag() {
        return result.getFlag();
    }

    /**
     * The type the flag argument should have been.
     *
     * @return The argument type.
     */
    @NotNull
    public Class<?> getArgumentType() {
        return result.getArgumentType();
    }

    /**
     * The argument that was typed by the user.
     *
     * @return The typed argument.
     */
    @NotNull
    public String getTypedArgument() {
        return result.getTypedArgument();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final InvalidFlagArgumentContext that = (InvalidFlagArgumentContext) o;
        return result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), result);
    }

    @Override
    public String toString() {
        return "InvalidFlagArgumentContext{" +
                "result=" + result +
                ", super=" + super.toString() + "}";
    }
}
