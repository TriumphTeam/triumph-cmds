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

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * A parent command means it's a simple holder of other commands.
 * The commands can either be a parent as well or simple sub commands.
 *
 * @param <S> The sender type.
 */
public abstract class InternalParentCommand<D, S> implements InternalCommand<D, S> {

    private final Map<String, InternalCommand<D, S>> commands = new HashMap<>();
    private final Map<String, InternalCommand<D, S>> commandAliases = new HashMap<>();
    private final CommandMeta meta;
    private final Settings<D, S> settings;
    private final MessageRegistry<S> messageRegistry;
    private final SenderExtension<D, S> senderExtension;

    public InternalParentCommand(final @NotNull CommandProcessor<D, S> processor) {
        final Settings.Builder<D, S> settingsBuilder = new Settings.Builder<>();
        processor.captureRequirements(settingsBuilder);
        this.meta = processor.createMeta(settingsBuilder);

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = processor.getCommandOptions().getCommandExtensions().getSenderExtension();

        this.settings = settingsBuilder.build();
    }

    /**
     * Add a new command to the maps.
     *
     * @param instance The instance of the command the commands came from.
     * @param commands A list of command to be added.
     */
    public void addCommands(
            final @NotNull Object instance,
            final @NotNull List<InternalCommand<D, S>> commands
    ) {
        for (final InternalCommand<D, S> command : commands) {
            // If it's a parent command with argument we add it
            if (command instanceof InternalBranchCommand && command.hasArguments()) {
                if (this.commands.containsKey(InternalCommand.PARENT_CMD_WITH_ARGS_NAME)) {
                    throw new CommandRegistrationException("Only one inner command with argument is allowed per command", instance.getClass());
                }
            }

            // Normal commands are added here
            this.commands.put(command.getName(), command);

            for (final String alias : command.getAliases()) {
                this.commandAliases.put(alias, command);
            }
        }
    }

    protected void findAndExecute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments
    ) throws Throwable {
        final InternalCommand<D, S> command = findCommand(sender, arguments, true);
        if (command == null) return;

        // Executing the command and catch all exceptions to rethrow with a better message
        if (command instanceof InternalBranchCommand) {
            ((InternalBranchCommand<D, S>) command).execute(sender, instanceSupplier, arguments);
            return;
        }

        final InternalLeafCommand<D, S> leafCommand = (InternalLeafCommand<D, S>) command;
        leafCommand.execute(sender, instanceSupplier, leafCommand.mapArguments(arguments));
    }

    @Override
    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    ) {
        final String argument = arguments.peek();
        if (argument == null) return emptyList();

        final InternalCommand<D, S> command = findCommand(sender, arguments, false);
        if (command == null) {
            return commands.entrySet().stream()
                    // Remove the default command from the list.
                    .filter(it -> !it.getValue().isDefault())
                    // Filter commands the sender can't see.
                    .filter(it -> it.getValue().getCommandSettings().testRequirements(sender, meta, senderExtension))
                    // Commands that match what the sender is typing.
                    .filter(it -> it.getKey().startsWith(argument))
                    // Only use the names.
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        return command.suggestions(sender, arguments);
    }


    protected @Nullable InternalCommand<D, S> findCommand(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments,
            final boolean message
    ) {
        final String name = arguments.peek();

        // Instant check for default
        final InternalCommand<D, S> defaultCommand = getCommandByName(InternalCommand.DEFAULT_CMD_NAME);

        // No argument passed
        if (name == null) {
            // No default command found, send a message and return null
            // If there is a default command, then return it
            if (defaultCommand == null && message) {
                messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(meta, ""));
            }

            return defaultCommand;
        }

        final InternalCommand<D, S> command = safelyGetCommandByName(name);
        if (command != null) {
            // Pop the command out of the argument list and returns it
            arguments.pop();
            return command;
        }

        if (defaultCommand == null || !defaultCommand.hasArguments()) {
            // No command found with the name [name]
            final InternalCommand<D, S> parentCommandWithArgument = getCommandByName(InternalCommand.PARENT_CMD_WITH_ARGS_NAME);
            if (parentCommandWithArgument == null && message) {
                messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(meta, name));
            }

            // Don't pop because it'll be the argument
            return parentCommandWithArgument;
        }

        // Default command is never null here
        return defaultCommand;
    }

    public @Nullable InternalCommand<D, S> getCommand(final @NotNull String name) {
        return commands.get(name);
    }

    @Override
    public @NotNull Settings<D, S> getCommandSettings() {
        return settings;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    public @NotNull Map<String, InternalCommand<D, S>> getCommands() {
        return commands;
    }

    protected @Nullable InternalCommand<D, S> safelyGetCommandByName(final @NotNull String key) {
        // Don't let the default command be retrieved by the name.
        if (key.equals(InternalCommand.DEFAULT_CMD_NAME)) return null;
        if (key.equals(InternalCommand.PARENT_CMD_WITH_ARGS_NAME)) return null;
        return getCommandByName(key);
    }

    protected @Nullable InternalCommand<D, S> getCommandByName(final @NotNull String key) {
        return commands.getOrDefault(key, commandAliases.get(key));
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    protected @NotNull MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    protected @NotNull Settings<D, S> getSettings() {
        return settings;
    }

    protected @NotNull SenderExtension<D, S> getSenderExtension() {
        return senderExtension;
    }
}
