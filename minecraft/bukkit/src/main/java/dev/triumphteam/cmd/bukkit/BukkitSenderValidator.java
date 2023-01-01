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

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.subcommand.OldSubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Simple mapper than returns itself.
 */
class BukkitSenderValidator implements SenderValidator<CommandSender> {

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Set<Class<? extends @NotNull CommandSender>> getAllowedSenders() {
        return ImmutableSet.of(CommandSender.class, ConsoleCommandSender.class, Player.class);
    }

    @Override
    public boolean validate(
            final @NotNull MessageRegistry<CommandSender> messageRegistry,
            final @NotNull OldSubCommand<CommandSender> subCommand,
            final @NotNull CommandSender sender
    ) {
        final Class<? extends CommandSender> senderClass = subCommand.getSenderType();

        if (Player.class.isAssignableFrom(senderClass) && !(sender instanceof Player)) {
            messageRegistry.sendMessage(
                    BukkitMessageKey.PLAYER_ONLY,
                    sender,
                    new DefaultMessageContext(subCommand.getParentName(), subCommand.getName())
            );
            return false;
        }

        if (ConsoleCommandSender.class.isAssignableFrom(senderClass) && !(sender instanceof ConsoleCommandSender)) {
            messageRegistry.sendMessage(
                    BukkitMessageKey.CONSOLE_ONLY,
                    sender,
                    new DefaultMessageContext(subCommand.getParentName(), subCommand.getName())
            );
            return false;
        }

        return true;
    }
}
