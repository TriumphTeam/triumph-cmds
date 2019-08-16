package me.mattstudios.framework;

import me.mattstudios.framework.annotations.Default;
import me.mattstudios.framework.annotations.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler extends Command {

    private CommandBase command;

    private Map<String, Method> methods;
    private Method defaultMethod;

    public CommandHandler(CommandBase command, String commandName, List<String>aliases) {
        super(commandName);
        this.command = command;
        setAliases(aliases);

        methods = new HashMap<>();

        for (Method method : command.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Default.class) && !method.isAnnotationPresent(SubCommand.class)) continue;
            if (method.isAnnotationPresent(Default.class)) defaultMethod = method;
            methods.put(method.getAnnotation(SubCommand.class).value(), method);
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {

        for (String subCommand : methods.keySet()) {
            commandSender.sendMessage(subCommand);
            commandSender.sendMessage(methods.get(subCommand).getName());
            commandSender.sendMessage(String.valueOf(defaultMethod == null));
        }

        return false;
    }
}
