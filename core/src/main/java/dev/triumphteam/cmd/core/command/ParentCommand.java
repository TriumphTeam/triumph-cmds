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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A parent command means it's a simple holder of other commands.
 * The commands can either be a parent as well or simple sub commands.
 *
 * @param <S> The sender type.
 */
public interface ParentCommand<S> extends Command {

    /**
     * @return The map with all the commands it holds.
     */
    @NotNull Map<String, ExecutableCommand<S>> getCommands();

    /**
     * @return The map with all the command alias it holds.
     */
    @NotNull Map<String, ExecutableCommand<S>> getCommandAliases();

    /**
     * @return A parent command with arguments as a sub command. Null if none exists.
     */
    @Nullable ExecutableCommand<S> getParentCommandWithArgument();

    /**
     * Add a new command to the maps.
     *
     * @param subCommand The sub command to be added.
     * @param isAlias    Whether it is an alias.
     */
    void addSubCommand(final @NotNull ExecutableCommand<S> subCommand, final boolean isAlias);

    @Override
    default boolean isDefault() {
        return false;
    }

    @Override
    default boolean hasArguments() {
        return !getCommands().isEmpty();
    }

    /**
     * Small abstraction to reduce repetition in the execution.
     *
     * @param name The name of the command, normally from {@link #nameFromArguments}.
     * @param size The size of arguments passed to the execution.
     * @return The default command, a sub command, or a parent command with arguments, and lastly null if none exist.
     */
    default @Nullable ExecutableCommand<S> getSubCommand(final @NotNull String name, final int size) {
        ExecutableCommand<S> subCommand = getDefaultSubCommand();

        if (subCommand == null || subCommandExists(name)) {
            subCommand = getSubCommand(name);
        }

        if (subCommand == null || (size > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            return getParentCommandWithArgument();
        }

        return subCommand;
    }

    /**
     * Gets the name of the command from the arguments given.
     *
     * @param arguments The list of arguments.
     * @return The name or an empty string if there are no arguments.
     */
    default @NotNull String nameFromArguments(final @NotNull List<String> arguments) {
        return arguments.size() > 0 ? arguments.get(0) : "";
    }

    default @Nullable ExecutableCommand<S> getDefaultSubCommand() {
        return getCommands().get(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME);
    }

    default @Nullable ExecutableCommand<S> getSubCommand(final @NotNull String key) {
        final ExecutableCommand<S> subCommand = getCommands().get(key);
        if (subCommand != null) return subCommand;
        return getCommandAliases().get(key);
    }

    default boolean subCommandExists(final @NotNull String key) {
        return getCommands().containsKey(key) || getCommandAliases().containsKey(key);
    }
}
