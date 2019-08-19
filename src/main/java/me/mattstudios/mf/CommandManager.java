package me.mattstudios.mf;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.components.CompletionHandler;
import me.mattstudios.mf.components.ParameterTypes;
import me.mattstudios.mf.exceptions.NoCommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private JavaPlugin plugin;

    // List of commands;
    private Map<String, CommandHandler> commands;

    private ParameterTypes parameterTypes;
    private CompletionHandler completionHandler;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;

        commands = new HashMap<>();
        parameterTypes = new ParameterTypes();
        completionHandler = new CompletionHandler();
    }

    /**
     * Gets the parameter types class to register new ones and to check too.
     *
     * @return The parameter types class.
     */
    public ParameterTypes getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Gets the completion handler class, which handles all the command completions in the plugin.
     *
     * @return The completion handler.
     */
    public CompletionHandler getCompletionHandler() {
        return completionHandler;
    }

    /**
     * Registers a command.
     *
     * @param command The command class to register.
     */
    public void register(CommandBase command) {
        Class commandClass = command.getClass();

        // Checks for the command annotation.
        if (!commandClass.isAnnotationPresent(Command.class))
            throw new NoCommandException("Class " + command.getClass().getName() + " needs to have @Command!");

        // Gets the command annotation value.
        String commandName = ((Command) commandClass.getAnnotation(Command.class)).value();
        String[] aliases = new String[0];

        //Checks if the class has some alias and adds them.
        if (commandClass.isAnnotationPresent(Alias.class))
            aliases = ((Alias) commandClass.getAnnotation(Alias.class)).value();

        // Used to get the command map to register the commands.
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            CommandHandler commandHandler;
            if (commands.containsKey(commandName)) {
                commands.get(commandName).addSubCommands(command);
                return;
            }

            commandHandler = new CommandHandler(parameterTypes, completionHandler, command, commandName, Arrays.asList(aliases));
            commandMap.register(plugin.getName(), commandHandler);

            // Puts the handler in the list to unregister later.
            commands.put(commandName, commandHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterAll() {
        commands.clear();
    }
}
