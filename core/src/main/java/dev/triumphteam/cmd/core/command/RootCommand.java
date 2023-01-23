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
package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.Map;
import java.util.function.Supplier;

public class RootCommand<D, S> extends ParentCommand<D, S> {

    private final String name;
    private final String syntax;

    public RootCommand(final @NotNull RootCommandProcessor<D, S> processor) {
        super(processor);

        this.name = processor.getName();
        this.syntax = "/" + name;
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments,
            final @NotNull Map<String, Object> extra
    ) {
        final Command<S> command = findCommand(sender, arguments);
        if (command == null) return;

        // Executing the command and catch all exceptions to rethrow with better message
        try {
            command.execute(
                    sender,
                    null,
                    arguments,
                    extra
            );
        } catch (final @NotNull Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getSyntax() {
        return syntax;
    }

    @Override
    public boolean hasArguments() {
        return false;
    }
}
