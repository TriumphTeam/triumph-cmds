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
package dev.triumphteam.cmd.bukkit;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extention.ValidationResult;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Simple mapper than returns itself.
 */
class BukkitSenderExtension implements SenderExtension.Default<CommandSender> {

    @Override
    public @NotNull Set<Class<? extends @NotNull CommandSender>> getAllowedSenders() {
        return ImmutableSet.of(CommandSender.class, ConsoleCommandSender.class, Player.class);
    }

    @Override
    public @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(
            final @NotNull CommandMeta meta,
            final @NotNull Class<?> allowedSender,
            final @NotNull CommandSender sender
    ) {
        if (Player.class.isAssignableFrom(allowedSender) && !(sender instanceof Player)) {
            return invalid(BukkitMessageKey.PLAYER_ONLY);
        }

        if (ConsoleCommandSender.class.isAssignableFrom(allowedSender) && !(sender instanceof ConsoleCommandSender)) {
            return invalid(BukkitMessageKey.CONSOLE_ONLY);
        }

        return valid();
    }
}
