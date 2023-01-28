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
package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extention.command.CommandSettings;
import dev.triumphteam.cmd.core.extention.meta.CommandMetaContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Representation of a command.
 * Not all commands are executable directly.
 * Some implementations will have listeners inside that'll trigger the execution.
 */
public interface Command<D, S> extends CommandMetaContainer {

    // TODO
    void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments,
            final @NotNull Map<String, Object> extra
    ) throws Throwable;

    @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    );

    @NotNull CommandSettings<D, S> getCommandSettings();

    /**
     * @return The name of the command.
     */
    @NotNull String getName();

    /**
     * @return Whether this is a "default" command, meaning it represents the class itself and is not separate.
     */
    boolean isDefault();

    /**
     * @return Whether the command has arguments.
     */
    boolean hasArguments();

    /**
     * @return The command's syntax.
     */
    @NotNull String getSyntax();
}
