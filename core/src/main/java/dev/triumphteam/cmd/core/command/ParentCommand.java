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

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * A parent command means it's a simple holder of other commands.
 * The commands can either be a parent as well or simple sub commands.
 *
 * @param <S> The sender type.
 */
public abstract class ParentCommand<D, S> implements Command<S> {

    private final Map<String, Command<S>> commands = new HashMap<>();
    private final Map<String, Command<S>> commandAliases = new HashMap<>();

    private final CommandMeta meta;

    // Single parent command with argument
    private Command<S> parentCommandWithArgument;

    private final MessageRegistry<S> messageRegistry;

    public ParentCommand(final @NotNull CommandProcessor<D, S> processor) {
        this.meta = processor.createMeta();
        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
    }

    @Override
    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    ) {
        final String argument = arguments.peek();
        if (argument == null) return emptyList();

        final Command<S> command = findCommand(sender, arguments);

        if (command == null) {
            return commands.entrySet().stream()
                    .filter(it -> !it.getValue().isDefault())
                    .filter(it -> it.getKey().startsWith(argument))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        return command.suggestions(sender, arguments);
    }

    /**
     * Add a new command to the maps.
     *
     * @param instance The instance of the command the commands came from.
     * @param command  The sub command to be added.
     * @param isAlias  Whether it is an alias.
     */
    public void addCommand(
            final @NotNull Object instance,
            final @NotNull Command<S> command,
            final boolean isAlias
    ) {
        // If it's a parent command with argument we add it
        if (command instanceof ParentSubCommand && command.hasArguments()) {
            if (parentCommandWithArgument != null) {
                throw new CommandRegistrationException("Only one inner command with argument is allowed per command", instance.getClass());
            }

            parentCommandWithArgument = command;
            return;
        }

        // Normal commands are added here
        commands.put(command.getName(), command);

        // TODO ALIAS
    }

    protected @Nullable Command<S> findCommand(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    ) {
        final String name = arguments.peek();

        // Instant check for default
        final Command<S> defaultCommand = getDefaultCommand();

        // No argument passed
        if (name == null) {
            // No default command found, send message and return null
            // If there is default command then return it
            if (defaultCommand == null) {
                messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(meta, ""));
            }

            return defaultCommand;
        }

        final Command<S> command = getCommandByName(name);
        if (command != null) {
            // Pop command out of arguments list and returns it
            arguments.pop();
            return command;
        }

        if (defaultCommand == null || !defaultCommand.hasArguments()) {
            // No command found with the name [name]
            if (parentCommandWithArgument == null) {
                messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(meta, name));
            }

            // Don't pop because it'll be the argument
            return parentCommandWithArgument;
        }

        // Default command is never null here
        return defaultCommand;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    protected @Nullable Command<S> getDefaultCommand() {
        return commands.get(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME);
    }

    protected @Nullable Command<S> getCommandByName(final @NotNull String key) {
        return commands.getOrDefault(key, commandAliases.get(key));
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    protected @NotNull MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }
}
