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

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

public class InternalRootCommand<D, S> extends InternalParentCommand<D, S> {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;

    public InternalRootCommand(final @NotNull RootCommandProcessor<D, S> processor) {
        super(processor);

        this.name = processor.getName();
        this.description = processor.getDescription();
        this.aliases = processor.getAliases();
        this.syntax = "/" + name;
    }

    public void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments
    ) {
        System.out.println("On start of root -> " + arguments);
        // Test all requirements before continuing
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        // Executing the command and catch all exceptions to rethrow with a better message
        try {
            System.out.println("Before moving forward -> " + arguments);
            findAndExecute(sender, null, arguments);
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
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return aliases;
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
