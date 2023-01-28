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
