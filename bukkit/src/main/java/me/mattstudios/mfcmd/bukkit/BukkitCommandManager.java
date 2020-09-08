package me.mattstudios.mfcmd.bukkit;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.CommandManager;
import me.mattstudios.mfcmd.base.MessageHandler;
import me.mattstudios.mfcmd.base.RequirementHandler;
import me.mattstudios.mfcmd.base.annotations.Command;
import me.mattstudios.mfcmd.base.components.CompletionResolver;
import me.mattstudios.mfcmd.base.exceptions.MfException;
import me.mattstudios.mfcmd.bukkit.components.BukkitMessageResolver;
import me.mattstudios.mfcmd.bukkit.components.BukkitRequirementResolver;
import me.mattstudios.mfcmd.bukkit.components.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class BukkitCommandManager extends CommandManager {

    @NotNull
    private final Plugin plugin;

    // The command map
    @Nullable
    private final CommandMap commandMap;

    // List of commands;
    @NotNull
    private Map<String, org.bukkit.command.Command> bukkitCommands = new HashMap<>();

    @NotNull
    private final MessageHandler<CommandSender> messageHandler = new MessageHandler<>();

    @NotNull
    private final RequirementHandler<CommandSender> requirementHandler = new RequirementHandler<>();

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

        registerCompletion("#players", input -> Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList()));
        registerCompletion("#empty", input -> Collections.emptyList());
        registerCompletion("#range", input -> {
            final String s = String.valueOf(input);

            if (s.contains("class"))
                return IntStream.rangeClosed(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());

            if (!s.contains("-"))
                return IntStream.rangeClosed(1, Integer.parseInt(s)).mapToObj(Integer::toString).collect(Collectors.toList());

            final String[] minMax = s.split("-");
            final int[] range = IntStream.rangeClosed(Integer.parseInt(minMax[0]), Integer.parseInt(minMax[1])).toArray();

            final List<String> rangeList = new ArrayList<>();

            for (int number : range) {
                rangeList.add(String.valueOf(number));
            }

            return rangeList;
        });
        registerCompletion("#enum", input -> {
            // noinspection unchecked
            final Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) input;
            final List<String> values = new ArrayList<>();

            for (Enum<?> enumValue : enumCls.getEnumConstants()) {
                values.add(enumValue.name());
            }

            values.sort(String.CASE_INSENSITIVE_ORDER);
            return values;
        });

    }

    @Override
    public void register(@NotNull final CommandBase... commands) {
        for (final CommandBase command : commands) {
            register(command);
        }
    }

    @Override
    public void register(@NotNull final CommandBase command) {
        if (commandMap == null) return;

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
            BukkitCommandHandler commandHandler = (BukkitCommandHandler) getCommands().get(commandName);
            if (commandHandler != null) {
                commandHandler.addSubCommands(command);
                return;
            }

            // Sets the message handler to be used in the command class
            //command.setMessageHandler(messageHandler);

            // Creates the command handler
            //commandHandler = new BukkitCommandHandler(getParameterHandler(), completionHandler, messageHandler, command, commandName, aliases, hideTab, completePlayers);
            commandHandler = new BukkitCommandHandler(getParameterHandler(), messageHandler, requirementHandler, getCompletionHandler(), command, commandName, aliases, true, true);

            // Registers the command
            commandMap.register(commandName, plugin.getName(), commandHandler);
            System.out.println(commandName);

            // Puts the handler in the list to unregister later.
            getCommands().put(commandName, commandHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerMessage(@NotNull final String id, @Nullable final BukkitMessageResolver messageResolver) {
        messageHandler.register(id, messageResolver);
    }

    public void registerRequirement(@NotNull final String id, @Nullable final BukkitRequirementResolver requirementResolver) {
        requirementHandler.register(id, requirementResolver);
    }

    public void registerCompletion(@NotNull final String id, @Nullable final CompletionResolver completionResolver) {
        getCompletionHandler().register(id, completionResolver);
    }

    @Nullable
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
