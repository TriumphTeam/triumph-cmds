package me.mattstudios.mf;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.components.CommandData;
import me.mattstudios.mf.components.ParameterTypes;
import me.mattstudios.mf.exceptions.NoParamException;
import me.mattstudios.mf.exceptions.NoSenderParamException;
import me.mattstudios.mf.exceptions.UnregisteredParamException;
import me.mattstudios.mf.exceptions.WrongParamException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandHandler extends Command {

    private CommandBase command;

    private Map<String, CommandData> methods;

    private ParameterTypes parameterTypes;

    CommandHandler(ParameterTypes parameterTypes, CommandBase command, String commandName, List<String> aliases) {
        super(commandName);
        this.command = command;
        this.parameterTypes = parameterTypes;
        setAliases(aliases);

        methods = new HashMap<>();

        // Iterates through all the methods in the class.
        for (Method method : command.getClass().getDeclaredMethods()) {
            // Checks if the method is public and if it is annotated by @Default or @SubCommand.
            if ((!method.isAnnotationPresent(Default.class) && !method.isAnnotationPresent(SubCommand.class)) || !Modifier.isPublic(method.getModifiers()))
                continue;

            // Checks if default method has no parameters.
            if (method.getParameterCount() == 0)
                throw new NoParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " - needs to have Parameters!");

            // Checks if the fist parameter is either a player or a sender.
            if (!method.getParameterTypes()[0].getTypeName().equals(CommandSender.class.getTypeName()) && !method.getParameterTypes()[0].getTypeName().equals(Player.class.getTypeName()))
                throw new NoSenderParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " - first parameter needs to be a CommandSender or a Player!");

            // Starts the command data object.
            CommandData commandData = new CommandData();
            commandData.setMethod(method);
            // Sets the first parameter as either player or command sender.
            commandData.setFirstParam(method.getParameterTypes()[0]);

            // Checks if the parameters in class are registered.
            for (int i = 1; i < method.getParameterTypes().length; i++) {
                Class clss = method.getParameterTypes()[i];
                if (!clss.isEnum() && !parameterTypes.isRegisteredType(clss)) {
                    throw new UnregisteredParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " contains unregistered parameter types!");
                }
                commandData.getParams().add(clss);
            }

            // Checks if it is a default method.
            if (method.isAnnotationPresent(Default.class)) {
                commandData.setDef(true);
                // Checks if there is more than one parameters in the default method.
                if (commandData.getParams().size() != 0)
                    throw new WrongParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " - Default method cannot have more than one parameter!");
            }

            // Checks if permission annotation is present.
            if (method.isAnnotationPresent(Permission.class)) {
                // Checks whether the command sender has the permission set in the annotation.
                commandData.setPermission(method.getAnnotation(SubCommand.class).value());
            }

            // Checks for aliases.
            if (method.isAnnotationPresent(Alias.class)) {
                // Iterates through the alias and add each as a normal sub command.
                for (String alias : method.getAnnotation(Alias.class).value()) {
                    CommandData aliasCD = commandData;
                    if (aliasCD.isDef()) aliasCD.setDef(false);
                    methods.put(alias, aliasCD);
                }
            }

            // puts the main method in the list.
            methods.put(method.getAnnotation(SubCommand.class).value(), commandData);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] arguments) {

        // Runs default command here as arguments are 0 or empty.
        if (arguments.length == 0 || arguments[0].isEmpty()) {

            CommandData commandData = getDefaultMethod();

            // Will not run if there is no default methods.
            if (commandData == null) return true;

            // Checks if permission annotation is present.
            if (commandData.hasPermission()) {
                // Checks whether the command sender has the permission set in the annotation.
                if (!sender.hasPermission(commandData.getPermission())) {
                    // TODO Error handler later
                    sender.sendMessage("No permission!");
                    return true;
                }
            }

            // Checks if the command can be accessed from console
            if (!commandData.getFirstParam().getTypeName().equals(CommandSender.class.getTypeName()) && !(sender instanceof Player)) {
                // TODO Error handler later
                sender.sendMessage("Can't be console");
                return true;
            }

            // Executes all the commands.
            return executeCommand(commandData, sender, arguments, true);
        }

        // Checks if the sub command is registered or not.
        if (!methods.containsKey(arguments[0])) {
            // TODO Error handler later
            sender.sendMessage("Command doesn't exist!");
            return true;
        }

        // Gets the method from the list.
        CommandData commandData = methods.get(arguments[0]);

        // Checks if permission annotation is present.
        if (commandData.hasPermission()) {
            // Checks whether the command sender has the permission set in the annotation.
            if (!sender.hasPermission(commandData.getPermission())) {
                // TODO Error handler later
                sender.sendMessage("No permission!");
                return true;
            }
        }

        // Checks if the command can be accessed from console
        if (!commandData.getFirstParam().getTypeName().equals(CommandSender.class.getTypeName()) && !(sender instanceof Player)) {
            // TODO Error handler later
            sender.sendMessage("Can't be console");
            return true;
        }

        // Runs the command executor.
        return executeCommand(commandData, sender, arguments, false);
    }

    private boolean executeCommand(CommandData commandData, CommandSender sender, String[] arguments, boolean def) {
        try {

            Method method = commandData.getMethod();

            // Checks if it the command is default and remove the sub command argument one if it is not.
            List<String> argumentsList = new LinkedList<>(Arrays.asList(arguments));
            if (!def && argumentsList.size() > 0) argumentsList.remove(0);

            // Checks if it is a default type command with just sender and args.
            if (commandData.getParams().size() == 1
                    && commandData.getParams().get(0).getTypeName().equals(String[].class.getTypeName())) {
                method.invoke(command, sender, arguments);
                return true;
            }

            // Check if the method only has a sender as parameter.
            if (commandData.getParams().size() == 0 && argumentsList.size() == 0) {
                method.invoke(command, sender);
                return true;
            }

            // Checks for correct command usage.
            if (commandData.getParams().size() != argumentsList.size()
                    && !commandData.getParams().get(commandData.getParams().size() - 1).getTypeName().equals(String[].class.getTypeName())) {
                // TODO Error later
                sender.sendMessage("wrong usage");
                return true;
            }

            // Creates a list of the params to send.
            List<Object> invokeParams = new ArrayList<>();
            // Adds the sender as one of the params.
            invokeParams.add(sender);

            // Iterates through all the parameters to check them.
            for (int i = 0; i < commandData.getParams().size(); i++) {
                Class parameter = commandData.getParams().get(i);

                Object result;
                // Checks weather the parameter is an enum, because it needs to be sent as Enum.class.
                if (parameter.isEnum())
                    result = parameterTypes.getTypeResult(Enum.class, argumentsList.get(i), sender, parameter);
                else result = parameterTypes.getTypeResult(parameter, argumentsList.get(i), sender);

                // Will be null if error occurs.
                if (result == null) return true;
                invokeParams.add(result);
            }

            Object[] invokeArray = invokeParams.toArray();

            method.invoke(command, invokeArray);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {

        if (args.length == 1) {
            List<String> commandNames = new ArrayList<>();

            if (!args[0].equals("")) {
                for (String commandName : methods.keySet()) {
                    if (!commandName.startsWith(args[0].toLowerCase())) continue;
                    commandNames.add(commandName);
                }
            } else {
                commandNames = new ArrayList<>(methods.keySet());;
            }

            Collections.sort(commandNames);

            return commandNames;
        }

        if (args.length == 2) sender.sendMessage(String.valueOf(2));
        if (args.length == 3) sender.sendMessage(String.valueOf(3));
        if (args.length == 4) sender.sendMessage(String.valueOf(4));

        return super.tabComplete(sender, alias, args);
    }

    private CommandData getDefaultMethod() {
        for (String subCommand : methods.keySet()) {
            if (methods.get(subCommand).isDef()) return methods.get(subCommand);
        }
        return null;
    }
}
