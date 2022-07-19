package dev.triumphteam.cmd.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public final class CommandPermission {

    private final String node;
    private final PermissionDefault permissionDefault;
    private final String description;

    public CommandPermission(
            @NotNull final String node,
            @NotNull final String description,
            @NotNull final PermissionDefault permissionDefault
    ) {
        this.node = node;
        this.description = description;
        this.permissionDefault = permissionDefault;
    }

    /**
     * Register the {@link Permission} to the server and return the node
     */
    public void register() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        // Check if permission already registered
        final Permission permission = pluginManager.getPermission(node);
        if (permission != null) return;
        pluginManager.addPermission(new Permission(node, description, permissionDefault));
    }

    @NotNull
    public String getNode() {
        return node;
    }

    public boolean hasPermission(@NotNull final CommandSender sender) {
        return sender.hasPermission(node);
    }

}
