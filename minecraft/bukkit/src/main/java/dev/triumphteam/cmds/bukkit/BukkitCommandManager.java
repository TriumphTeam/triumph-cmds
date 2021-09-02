/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.cmds.bukkit;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.cmds.bukkit.factory.BukkitCommandFactory;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.CommandManager;
import dev.triumphteam.core.command.Command;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class BukkitCommandManager extends CommandManager<CommandSender> {

    private final Plugin plugin;
    private final CommandMap commandMap;

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, org.bukkit.command.Command> bukkitCommands;

    public BukkitCommandManager(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.commandMap = commandMap();
        this.bukkitCommands = bukkitCommands(commandMap);
        //registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(String.valueOf(arg)));
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand command) {
        final BukkitCommand bukkitCommand = new BukkitCommandFactory(command, getArgumentRegistry(), getRequirementRegistry()).create();

        // TODO multiple classes
        if (!bukkitCommand.addSubCommands(command)) {
            return;
        }

        final String commandName = bukkitCommand.getName();

        final org.bukkit.command.Command oldCommand = commandMap.getCommand(commandName);

        // From ACF (https://github.com/aikar/commands)
        // To allow commands to be registered on the plugin.yml
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == plugin) {
            unregisterCommand(command);
        }

        // Registering
        commandMap.register(commandName, plugin.getName(), bukkitCommand);
        commands.put(commandName, bukkitCommand);
    }

    // TODO remove the need to create a new command
    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {
        final BukkitCommand bukkitCommand = new BukkitCommandFactory(command, getArgumentRegistry(), getRequirementRegistry()).create();
        bukkitCommands.remove(bukkitCommand.getName());
        bukkitCommand.unregister(commandMap);
        commands.remove(bukkitCommand.getName());
    }

    /**
     * Gets the Command Map to register the commands
     *
     * @return The Command Map
     */
    @NotNull
    private CommandMap commandMap() {
        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            return (CommandMap) getCommandMap.invoke(server);
        } catch (final Exception ignored) {
            // TODO make this message better
            throw new CommandRegistrationException("Could not get Command Map, Commands won't be registered!");
        }
    }

    @NotNull
    private Map<String, org.bukkit.command.Command> bukkitCommands(@NotNull final CommandMap commandMap) {
        try {
            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);
            //noinspection unchecked
            return (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new CommandRegistrationException("Could not get Command Map, Commands won't be registered!");
        }
    }

}
