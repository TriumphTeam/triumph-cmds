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

import dev.triumphteam.cmd.core.extention.command.Settings;
import dev.triumphteam.cmd.core.extention.meta.CommandMetaContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Representation of a command.
 * Commands can either be command holders and commands themselves.
 * Some holders are {@link ParentCommand}s.
 * And actual commands can be for example {@link SubCommand}.
 */
public interface Command<D, S> extends CommandMetaContainer {

    /**
     * Execute the command with the needed arguments, instance, and some extra.
     *
     * @param sender           The sender of the command.
     * @param instanceSupplier A supplier for which instance will be needed when invoking the command.
     * @param arguments        A {@link Deque} with the arguments passed, these are consumed on each step.
     * @param extra            A map of extra details to pass down to the command.
     * @throws Throwable Anything that goes wrong with the execution.
     */
    void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments,
            final @NotNull Map<String, Object> extra
    ) throws Throwable;

    /**
     * Create a list of suggestion strings to return to the platform requesting it.
     *
     * @param sender    Rhe sender to get suggestions for.
     * @param arguments The arguments used in the suggestion.
     * @return A list of valid suggestions for the command.
     */
    @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    );

    @NotNull Settings<D, S> getCommandSettings();

    /**
     * @return The name of the command.
     */
    @NotNull String getName();

    /**
     * @return A list with all of its aliases.
     */
    @NotNull List<String> getAliases();

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
