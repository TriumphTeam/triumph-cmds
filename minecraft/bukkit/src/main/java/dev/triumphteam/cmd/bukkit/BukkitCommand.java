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

import dev.triumphteam.cmd.core.command.ParentCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.subcommand.OldSubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class BukkitCommand<S> extends org.bukkit.command.Command implements ParentCommand<S> {

    private final SenderMapper<CommandSender, S> senderMapper;
    private final MessageRegistry<S> messageRegistry;

    public BukkitCommand(
            final @NotNull BukkitCommandProcessor<S> processor,
            final @NotNull SenderMapper<CommandSender, S> senderMapper,
            final @NotNull MessageRegistry<S> messageRegistry
    ) {
        super(processor.getName());

        this.description = processor.getDescription();
        this.senderMapper = senderMapper;
        this.messageRegistry = messageRegistry;
    }

    /**
     * {@inheritDoc}
     *
     * @throws CommandExecutionException If the sender mapper returns null.
     */
    @Override
    public boolean execute(
            final @NotNull CommandSender sender,
            final @NotNull String commandLabel,
            final @NotNull String @NotNull [] args
    ) {
        final List<String> arguments = Arrays.asList(args);
        final int argumentSize = arguments.size();

        final OldSubCommand<S> subCommand = getSubCommand(arguments);

        final S mappedSender = senderMapper.map(sender);
        if (mappedSender == null) {
            throw new CommandExecutionException("Invalid sender. Sender mapper returned null");
        }

        if (subCommand == null || (argumentSize > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            final String name = argumentSize == 0 ? dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME : arguments.get(0);
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(getName(), name));
            return true;
        }

        // TODO: Better command check that is more abstracted
        /*final CommandPermission permission = subCommand.getPermission();
        if (!CommandPermission.hasPermission(sender, permission)) {
            messageRegistry.sendMessage(BukkitMessageKey.NO_PERMISSION, mappedSender, new NoPermissionMessageContext(getName(), subCommand.getName(), permission));
            return true;
        }*/
        subCommand.execute(mappedSender, !subCommand.isDefault() ? arguments.subList(1, argumentSize) : arguments);
        return true;
    }

    @Override
    public @NotNull Map<String, OldSubCommand<S>> getCommands() {
        return null;
    }

    @Override
    public @NotNull Map<String, OldSubCommand<S>> getCommandAliases() {
        return null;
    }
}
