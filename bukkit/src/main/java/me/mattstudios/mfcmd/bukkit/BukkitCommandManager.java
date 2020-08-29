package me.mattstudios.mfcmd.bukkit;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.CommandManager;
import me.mattstudios.mfcmd.base.MessageHandler;
import me.mattstudios.mfcmd.base.annotations.Command;
import me.mattstudios.mfcmd.base.exceptions.MfException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class BukkitCommandManager extends CommandManager {

    private final Plugin plugin;

    // The command map
    private final CommandMap commandMap;

    // List of commands;
    private Map<String, org.bukkit.command.Command> bukkitCommands = new HashMap<>();

    private final MessageHandler<CommandSender> messageHandler = new MessageHandler<>();

    public BukkitCommandManager(@NotNull final Plugin plugin) {
        this.plugin = plugin;


        this.commandMap = getCommandMap();

        // Registering Bukkit specific parameters
        registerParameter(Player.class, Bukkit::getPlayer);
        registerParameter(Material.class, Material::matchMaterial);
        registerParameter(Sound.class, arg -> Arrays.stream(Sound.values()).map(Enum::name).filter(name -> name.equalsIgnoreCase(arg)).findFirst().orElse(null));
        registerParameter(World.class, Bukkit::getWorld);

        registerMessage("cmd.no.permission", sender -> sender.sendMessage(BukkitUtils.color("&cYou don't have permission to execute this command!")));
        registerMessage("cmd.no.console", sender -> sender.sendMessage(BukkitUtils.color("&cCommand can't be executed through the console!")));
        registerMessage("cmd.no.player", sender -> sender.sendMessage(BukkitUtils.color("&cCommand can only be executed through the console!")));
        registerMessage("cmd.no.exists", sender -> sender.sendMessage(BukkitUtils.color("&cThe command you're trying to use doesn't exist!")));
        registerMessage("cmd.wrong.usage", sender -> sender.sendMessage(BukkitUtils.color("&cWrong usage for the command!")));

    }

    @Override
    public void register(final @NotNull CommandBase... commands) {
        for (final CommandBase command: commands) {
            register(command);
        }
    }

    @Override
    public void register(@NotNull final CommandBase command) {
        final Class<?> commandClass = command.getClass();

        final List<String> aliases = new ArrayList<>();
        String commandName;

        // Checks for the command annotation.
        if (!commandClass.isAnnotationPresent(Command.class)) {
            commandName = command.getCommand();
            if (commandName == null) {
                throw new MfException("No \"command\" was introduced for the class " + command.getClass().getName());
            }

            aliases.addAll(command.getAliases());
        } else {
            final String[] commands = commandClass.getAnnotation(Command.class).value();
            commandName = commands[0];

            if (commandName.trim().isEmpty()) {
                throw new MfException("Command cannot be empty, in class " + command.getClass().getName());
            }

            Collections.addAll(aliases, commands);
            aliases.remove(0);
        }

        org.bukkit.command.Command oldCommand = commandMap.getCommand(commandName);

        // From ACF
        // To allow commands to be registered on the plugin.yml
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == this.plugin) {
            bukkitCommands.remove(commandName);
            oldCommand.unregister(commandMap);
        }

        // Used to get the command map to register the commands.
        try {
            final BukkitCommandHandler commandHandler;
            if (getCommands().get(commandName) != null) {
                //commands.get(commandName).addSubCommands(command);
                return;
            }

            // Sets the message handler to be used in the command class
            //command.setMessageHandler(messageHandler);

            // Creates the command handler
            //commandHandler = new BukkitCommandHandler(getParameterHandler(), completionHandler, messageHandler, command, commandName, aliases, hideTab, completePlayers);
            commandHandler = new BukkitCommandHandler(getParameterHandler(), messageHandler, command, commandName, aliases, true, true);

            // Registers the command
            commandMap.register(commandName, plugin.getName(), commandHandler);
            System.out.println(commandName);

            // Puts the handler in the list to unregister later.
            getCommands().put(commandName, commandHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerMessage(final @NotNull String id, final @NotNull BukkitMessageResolver messageResolver) {
        messageHandler.register(id, messageResolver);
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            commandMap = (CommandMap) getCommandMap.invoke(server);

            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);

            //noinspection unchecked
            this.bukkitCommands = (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (final Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not get Command Map, Commands won't be registered!");
        }

        return commandMap;
    }

}
