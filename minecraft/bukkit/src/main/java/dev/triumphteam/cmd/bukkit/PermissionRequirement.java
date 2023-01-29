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

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class PermissionRequirement<S> implements Requirement<CommandSender, S> {

    private final CommandPermission permission;

    public PermissionRequirement(final @NotNull CommandPermission permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<CommandSender, S> senderMapper
    ) {
        return permission.hasPermission(senderMapper.mapBackwards(sender));
    }

    @Override
    public void onDeny(
            final @NotNull S sender,
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull CommandMeta meta
    ) {
        messageRegistry.sendMessage(BukkitMessageKey.NO_PERMISSION, sender, new NoPermissionMessageContext(meta, permission));
    }

    @Override
    public String toString() {
        return "PermissionRequirement{" +
                "permission=" + permission +
                '}';
    }
}
