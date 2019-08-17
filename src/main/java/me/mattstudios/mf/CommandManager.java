package me.mattstudios.mf;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.exceptions.NoCommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private JavaPlugin plugin;
    private List<CommandBase> commands;

    private ParameterTypes parameterTypes;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;

        parameterTypes = new ParameterTypes();
    }

    public void register(CommandBase command) {
        Class commandClass = command.getClass();

        if (!commandClass.isAnnotationPresent(Command.class))
            throw new NoCommandException("Class " + command.getClass().getName() + " needs to have @Command!");

        String commandName = ((Command) commandClass.getAnnotation(Command.class)).value();
        String[] aliases = new String[0];
        if (commandClass.isAnnotationPresent(Alias.class))
            aliases = ((Alias) commandClass.getAnnotation(Alias.class)).value();

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(plugin.getName(), new CommandHandler(parameterTypes, command, commandName, Arrays.asList(aliases)));
            
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
