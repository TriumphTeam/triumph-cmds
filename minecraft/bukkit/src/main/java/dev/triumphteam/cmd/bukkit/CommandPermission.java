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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data holder for the command's permission.
 * Including its default state and a description.
 */
public final class CommandPermission {

    private final List<String> nodes;
    private final PermissionDefault permissionDefault;
    private final String description;

    public CommandPermission(
            final @NotNull List<String> nodes,
            final @NotNull String description,
            final @NotNull PermissionDefault permissionDefault
    ) {
        this.nodes = nodes;
        this.description = description;
        this.permissionDefault = permissionDefault;

        register();
    }

    /**
     * Checks whether the {@link CommandSender} has the (nullable) {@link CommandPermission}.
     * <p>
     * The method simply checks if any of the following two things are {@code true}:
     * <ul>
     *     <li>The permission is {@code null}</li>
     *     <li>The sender has the permission</li>
     * </ul>
     *
     * @param sender     The main command sender.
     * @param permission The permission.
     * @return Whether the sender has permission to run the command.
     */
    public static boolean hasPermission(
            final @NotNull CommandSender sender,
            final @Nullable CommandPermission permission
    ) {
        return permission == null || permission.hasPermission(sender);
    }

    public @NotNull CommandPermission child(
            final @NotNull List<String> nodes,
            final @NotNull String description,
            final @NotNull PermissionDefault permissionDefault
    ) {
        final List<String> newNodes = this.nodes.stream()
                .flatMap(parent -> nodes.stream().map(node -> parent + "." + node))
                .collect(Collectors.toList());

        return new CommandPermission(newNodes, description, permissionDefault);
    }

    /**
     * Register the {@link Permission} to the server.
     */
    public void register() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        nodes.forEach(node -> {
            // Don't register if already registered
            final Permission permission = pluginManager.getPermission(node);
            if (permission != null) return;

            pluginManager.addPermission(new Permission(node, description, permissionDefault));
        });
    }

    /**
     * Gets the permission nodes.
     *
     * @return The permission nodes.
     */
    public @NotNull List<String> getNodes() {
        return nodes;
    }

    /**
     * Checks if the {@link CommandSender} has the permission to run the command.
     *
     * @param sender The main command sender.
     * @return Whether the sender has permission to run the command.
     */
    public boolean hasPermission(final @NotNull CommandSender sender) {
        return nodes.stream().anyMatch(sender::hasPermission);
    }

    @Override
    public String toString() {
        return "CommandPermission{" +
                "nodes=" + nodes +
                '}';
    }
}
