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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Context for when user types an invalid argument based on its type.
 */
public final class InvalidArgumentContext extends AbstractMessageContext {

    private final String argument;
    private final String name;
    private final Class<?> type;

    public InvalidArgumentContext(
            @NotNull final String command,
            @NotNull final String subCommand,
            @NotNull final String argument,
            @NotNull final String name,
            @NotNull final Class<?> type
    ) {
        super(command, subCommand);
        this.argument = argument;
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the invalid argument the user typed.
     *
     * @return The invalid argument.
     */
    @NotNull
    public String getTypedArgument() {
        return argument;
    }

    /**
     * Gets the name of the argument.
     * If compiled with <code>-parameters</code> argument, it'll show the actual parameter name.
     * If not compiled with it, names will look like <code>arg1, arg2, arg3</code>, etc.
     *
     * @return The argument name, should be equal to the parameter name.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the expected argument type.
     *
     * @return The argument type the user should have used.
     */
    @NotNull
    public Class<?> getArgumentType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final InvalidArgumentContext that = (InvalidArgumentContext) o;
        return argument.equals(that.argument) && name.equals(that.name) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), argument, name, type);
    }

    @Override
    public String toString() {
        return "InvalidArgumentContext{" +
                "argument='" + argument + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", super=" + super.toString() + "}";
    }
}
