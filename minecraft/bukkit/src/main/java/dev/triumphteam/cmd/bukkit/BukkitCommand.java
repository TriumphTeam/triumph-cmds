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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.command.ExecutableCommand;
import dev.triumphteam.cmd.core.command.ParentCommand;
import dev.triumphteam.cmd.core.command.ParentSubCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class BukkitCommand<S> extends Command implements ParentCommand<S> {

    private final Map<String, ExecutableCommand<S>> commands = new HashMap<>();

    private final MessageRegistry<S> messageRegistry;
    private final String syntax;

    private final CommandMeta meta;
    private ExecutableCommand<S> parentCommandWithArgument;
    private SenderExtension<CommandSender, S> senderExtension;

    BukkitCommand(
            final @NotNull RootCommandProcessor<CommandSender, S> processor,
            final @NotNull MessageRegistry<S> messageRegistry
    ) {
        super(processor.getName());

        this.messageRegistry = messageRegistry;
        this.meta = processor.createMeta();
        this.senderExtension = processor.getCommandOptions().getSenderExtension();

        this.syntax = "/" + getName();
    }

    @Override
    public void addSubCommand(final @NotNull ExecutableCommand<S> subCommand, final boolean isAlias) {
        // If it's a parent command with argument we add it
        if (subCommand instanceof ParentSubCommand && subCommand.hasArguments()) {
            if (parentCommandWithArgument != null) {
                throw new CommandRegistrationException("Only one inner command with argument is allowed per command.", subCommand.getInvocationInstance().getClass());
            }

            parentCommandWithArgument = subCommand;
            return;
        }

        commands.put(subCommand.getName(), subCommand);
    }

    @Override
    public boolean execute(
            @NotNull final CommandSender sender,
            @NotNull final String commandLabel,
            @NotNull final String[] args
    ) {
        final List<String> arguments = Arrays.asList(args);

        final String commandName = nameFromArguments(arguments);
        final ExecutableCommand<S> subCommand = getSubCommand(commandName, arguments.size());

        final S mappedSender = senderExtension.map(sender);

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new InvalidCommandContext(meta, commandName));
            return true;
        }

        // Executing the subcommand.
        try {
            subCommand.execute(
                    mappedSender,
                    commandName,
                    null,
                    !subCommand.isDefault() ? arguments.subList(1, arguments.size()) : arguments
            );
        } catch (final @NotNull Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }

        return true;
    }

    @Override
    public @NotNull String getSyntax() {
        return syntax;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommandAliases() {
        return commands;
    }

    @Override
    public @Nullable ExecutableCommand<S> getParentCommandWithArgument() {
        return parentCommandWithArgument;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
