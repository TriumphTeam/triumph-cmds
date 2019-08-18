package me.mattstudios.mf;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.exceptions.NoParamException;
import me.mattstudios.mf.exceptions.NoSenderParamException;
import me.mattstudios.mf.exceptions.UnregisteredParamException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandHandler extends Command {

    private CommandBase command;

    private Map<String, Method> methods;
    private Method defaultMethod;

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

            // Checks if the parameters in class are registered.
            for (int i = 1; i < method.getParameterTypes().length; i++) {
                if (!parameterTypes.isRegisteredType(method.getParameterTypes()[i]))
                    throw new UnregisteredParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " contains unregistered parameter types!");
            }

            // Checks if it is a default method.
            if (method.isAnnotationPresent(Default.class)) defaultMethod = method;

            // Checks for aliases.
            if (method.isAnnotationPresent(Alias.class)) {
                // Iterates through the alias and add each as a normal sub command.
                for (String alias : method.getAnnotation(Alias.class).value()) {
                    methods.put(alias, method);
                }
            }

            // puts the main method in the list.
            methods.put(method.getAnnotation(SubCommand.class).value(), method);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] arguments) {

        // Runs default command here as arguments are 0 or empty.
        if (arguments.length == 0 || arguments[0].isEmpty()) {
            // Will not run if there is no default methods.
            if (defaultMethod == null) return true;

            // Checks if permission annotation is present.
            if (defaultMethod.isAnnotationPresent(Permission.class)) {
                // Checks whether the command sender has the permission set in the annotation.
                if (!sender.hasPermission(defaultMethod.getAnnotation(SubCommand.class).value())) {
                    // Error handler later
                    sender.sendMessage("No permission!");
                    return true;
                }
            }

            // Checks if the command can be accessed from console
            if (!defaultMethod.getParameterTypes()[0].getTypeName().equals(CommandSender.class.getTypeName()) && !(sender instanceof Player)) {
                // Error handler later
                sender.sendMessage("Can't be console");
                return true;
            }

            // Executes all the commands.
            return executeCommand(defaultMethod, sender, arguments, true);
        }

        // Checks if the sub command is registered or not.
        if (!methods.containsKey(arguments[0])) {
            // Error handler later
            sender.sendMessage("Command doesn't exist!");
            return true;
        }

        // Gets the method from the list.
        Method method = methods.get(arguments[0]);

        // Checks if permission annotation is present.
        if (method.isAnnotationPresent(Permission.class)) {
            // Checks whether the command sender has the permission set in the annotation.
            if (!sender.hasPermission(method.getAnnotation(SubCommand.class).value())) {
                // Error handler later
                sender.sendMessage("No permission!");
                return true;
            }
        }

        // Checks if the command can be accessed from console
        if (!method.getParameterTypes()[0].getTypeName().equals(CommandSender.class.getTypeName()) && !(sender instanceof Player)) {
            // Error handler later
            sender.sendMessage("Can't be console");
            return true;
        }

        // Runs the command executor.
        return executeCommand(method, sender, arguments, false);
    }

    private boolean executeCommand(Method method, CommandSender sender, String[] arguments, boolean def) {
        try {
            // Removes the Player/CommandSender from the parameters list.
            List<Class> paramList = new LinkedList<>(Arrays.asList(method.getParameterTypes()));
            paramList.remove(0);

            System.out.println(paramList);

            // Checks if it the command is default and remove the sub command argument one if it is not.
            List<String> argumentsList = new LinkedList<>(Arrays.asList(arguments));
            if (!def && argumentsList.size() > 0) argumentsList.remove(0);

            // Checks if it is a default type command with just sender and args.
            if (paramList.size() == 1
                    && paramList.get(0).getTypeName().equals(String[].class.getTypeName())) {
                method.invoke(command, sender, arguments);
                return true;
            }

            // Check if the method only has a sender as parameter.
            if (paramList.size() == 0 && argumentsList.size() == 0) {
                method.invoke(command, sender);
                return true;
            }

            // Checks for correct command usage.
            if (paramList.size() != argumentsList.size()
                    && !paramList.get(paramList.size() - 1).getTypeName().equals(String[].class.getTypeName())) {
                System.out.println("wrong usage");
                return true;
            }

            List<Object> invokeParams = new ArrayList<>();
            invokeParams.add(sender);
            for (int i = 0; i < paramList.size(); i++) {
                invokeParams.add(parameterTypes.getTypeResult(paramList.get(i), argumentsList.get(i), (Player) sender));
            }

            Object[] invokeArray = invokeParams.toArray();

            method.invoke(command, invokeArray);
            return true;

            /*if (paramList.size() == 1 && paramList.get(0).getTypeName().equals(String[].class.getTypeName())){
                method.invoke(command, sender, arguments);
                return true;
            }

            if (paramList.size() > argumentsList.size()) System.out.println("wrong usage");

            System.out.println();

            List<Object> argumentsToPass = new ArrayList<>();
            argumentsToPass.add(sender);*/


            //method.invoke(command);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
