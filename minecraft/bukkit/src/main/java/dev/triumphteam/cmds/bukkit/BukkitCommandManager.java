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
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class BukkitCommandManager extends CommandManager {

    private final Plugin plugin;

    private final CommandMap commandMap;

    private final Map<String, Command> commands = new HashMap<>();
    private Map<String, org.bukkit.command.Command> bukkitCommands = new HashMap<>();

    public BukkitCommandManager(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.commandMap = getCommandMap();
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand command) {
        final BukkitCommand bukkitCommand = BukkitCommandFactory.of(command);

        // TODO multiple classes
        if (!bukkitCommand.addSubCommands(command)) {
            return;
        }

        final String commandName = bukkitCommand.getName();

        org.bukkit.command.Command oldCommand = commandMap.getCommand(commandName);

        // From ACF
        // To allow commands to be registered on the plugin.yml
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == plugin) {
            bukkitCommands.remove(commandName);
            oldCommand.unregister(commandMap);
        }

        // Registering
        commandMap.register(commandName, plugin.getName(), bukkitCommand);
        commands.put(bukkitCommand.getName(), bukkitCommand);
    }

    /**
     * Gets the Command Map to register the commands
     *
     * @return The Command Map
     */
    @NotNull
    private CommandMap getCommandMap() {
        final CommandMap commandMap;

        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            commandMap = (CommandMap) getCommandMap.invoke(server);

            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);

            //noinspection unchecked
            this.bukkitCommands = (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (final Exception ignored) {
            throw new CommandRegistrationException("Could not get Command Map, Commands won't be registered!");
        }

        return commandMap;
    }

}
