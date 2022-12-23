/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.message.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The default most keys will use, only contains the most basic data.
 */
public abstract class AbstractMessageContext implements MessageContext {

    private final String command;
    private final String subCommand;

    public AbstractMessageContext(final @NotNull String command, final @NotNull String subCommand) {
        this.command = command;
        this.subCommand = subCommand;
    }

    /**
     * Gets the command in which the error occurred.
     *
     * @return The command name.
     */
    @Override
    public @NotNull String getCommand() {
        return command;
    }

    /**
     * Gets the sub command in which the error occurred.
     *
     * @return The sub command name.
     */
    @Override
    public @NotNull String getSubCommand() {
        return subCommand;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractMessageContext that = (AbstractMessageContext) o;
        return command.equals(that.command) && Objects.equals(subCommand, that.subCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, subCommand);
    }

    @Override
    public @NotNull String toString() {
        return "AbstractMessageContext{" +
                "command='" + command + '\'' +
                ", subCommand='" + subCommand + '\'' +
                '}';
    }
}
