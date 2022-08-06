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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

/**
 * Data holder for the command's permission.
 * Including its default state and a description.
 */
final class CommandPermission {

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
     * Register the {@link Permission} to the server.
     */
    public void register() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        System.out.println(node);
        // Don't register if already registered
        final Permission permission = pluginManager.getPermission(node);
        if (permission != null) return;

        pluginManager.addPermission(new Permission(node, description, permissionDefault));
    }

    /**
     * Gets the permission node.
     *
     * @return The permission node.
     */
    @NotNull
    public String getNode() {
        return node;
    }

    /**
     * Checks if the {@link CommandSender} has the permission to run the command.
     *
     * @param sender The main command sender.
     * @return Whether the sender has permission to run the command.
     */
    public boolean hasPermission(@NotNull final CommandSender sender) {
        return sender.hasPermission(node);
    }
}
