/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
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

package me.mattstudios.mf.base;


import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.CompleteFor;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.annotations.Values;
import me.mattstudios.mf.annotations.WrongUsage;
import me.mattstudios.mf.base.components.CommandData;
import me.mattstudios.mf.exceptions.InvalidCompletionIdException;
import me.mattstudios.mf.exceptions.InvalidParamAnnotationException;
import me.mattstudios.mf.exceptions.InvalidParamException;
import me.mattstudios.mf.exceptions.SyntaxError;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static me.mattstudios.mf.base.components.MfUtil.color;

public final class CommandHandler extends Command {
    // Contains all the sub commands
    private final Map<String, CommandData> commands = new HashMap<>();

    // Handler for the parameter types
    private final ParameterHandler parameterHandler;
    // Handler for the command completions
    private final CompletionHandler completionHandler;
    // Handler for the message system
    private final MessageHandler messageHandler;

    // If should tab complete without perms or not
    private boolean hideTab;

    CommandHandler(final ParameterHandler parameterHandler, final CompletionHandler completionHandler,
                   final MessageHandler messageHandler, final CommandBase command,
                   final String commandName, final List<String> aliases, final boolean hideTab) {

        super(commandName);

        this.parameterHandler = parameterHandler;
        this.completionHandler = completionHandler;
        this.messageHandler = messageHandler;
        this.hideTab = hideTab;

        setAliases(aliases);

        addSubCommands(command);
    }

    void addSubCommands(final CommandBase command) {
        // Iterates through all the methods in the class.
        for (final Method method : command.getClass().getDeclaredMethods()) {

            final CommandData subCommand = new CommandData(command);

            // Checks if the method is public and if it is annotated by @Default or @SubCommand.
            if ((!method.isAnnotationPresent(Default.class) && !method.isAnnotationPresent(SubCommand.class)) || !Modifier.isPublic(method.getModifiers()))
                continue;

            // Checks if default method has no parameters.
            if (method.getParameterCount() == 0)
                throw new InvalidParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " - needs to have Parameters!");

            // Checks if the fist parameter is either a player or a sender.
            if (!CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) && !Player.class.isAssignableFrom(method.getParameterTypes()[0]))
                throw new InvalidParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " - first parameter needs to be a CommandSender or a Player!");

            // Starts the command data object.
            subCommand.setMethod(method);
            // Sets the first parameter as either player or command sender.
            subCommand.setFirstParam(method.getParameterTypes()[0]);

            // Checks if the parameters in class are registered.
            checkRegisteredParams(method, command, subCommand);

            // Checks if it's a default method.
            checkDefault(method, subCommand);

            // Checks if permission annotation is present.
            checkPermission(method, subCommand);

            // Checks if wrong usage annotation is present.
            checkWrongUsage(method, subCommand);

            // Check if optional parameter is present.
            checkOptionalParam(method, command, subCommand);

            // Checks for completion annotation in the method.
            checkMethodCompletion(method, command, subCommand);

            // Checks for completion on the parameters.
            checkParamCompletion(method, command, subCommand);

            // Checks for aliases.
            checkAlias(method, subCommand);

            // puts the main method in the list.
            if (!subCommand.isDefault() && method.isAnnotationPresent(SubCommand.class)) {
                final String name = method.getAnnotation(SubCommand.class).value().toLowerCase();
                subCommand.setName(name);
                commands.put(name, subCommand);
            }

            // Puts a default command in the list.
            if (subCommand.isDefault()) {
                subCommand.setName("default");
                commands.put("default", subCommand);
            }

            // Checks for a completion method
            checkCompletionMethod(command, subCommand);
        }
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] arguments) {

        CommandData subCommand = getDefaultSubCommand();

        if (arguments.length == 0 || arguments[0].isEmpty()) {

            // Will not run if there is no default methods.
            if (subCommand == null) return unknownCommand(sender);

            // Checks if permission annotation is present.
            // Checks whether the command sender has the permission set in the annotation.
            if (subCommand.hasPermissions() && !hasPermissions(sender, subCommand))
                return noPermission(sender);

            // Checks if the command can be accessed from console
            if (!CommandSender.class.equals(subCommand.getFirstParam()) && !(sender instanceof Player))
                return noConsole(sender);

            // Executes all the commands.
            return executeCommand(subCommand, sender, arguments);
        }

        // Sets the command to lower case so it can be typed either way.
        final String argCommand = arguments[0].toLowerCase();

        // Checks if the sub command is registered or not.
        if ((subCommand != null && subCommand.getParams().size() == 0) && (!commands.containsKey(argCommand) || getName().equalsIgnoreCase(argCommand)))
            return unknownCommand(sender);

        // Checks if the sub command is registered or not.
        if (subCommand == null && !commands.containsKey(argCommand)) return unknownCommand(sender);

        // Checks if the command is on the list, which means it's no longer a default command.
        if (commands.containsKey(argCommand)) subCommand = commands.get(argCommand);

        // Checks if permission annotation is present.
        // Checks whether the command sender has the permission set in the annotation.
        assert subCommand != null;
        if (subCommand.hasPermissions() && !hasPermissions(sender, subCommand))
            return noPermission(sender);

        // Checks if the command can be accessed from console
        if (!CommandSender.class.equals(subCommand.getFirstParam()) && !(sender instanceof Player))
            return noConsole(sender);


        // Runs the command executor.
        return executeCommand(subCommand, sender, arguments);
    }

    private boolean executeCommand(final CommandData subCommand, final CommandSender sender, final String[] arguments) {
        try {

            final Method method = subCommand.getMethod();

            // Checks if it the command is default and remove the sub command argument one if it is not.
            final List<String> argumentsList = new LinkedList<>(Arrays.asList(arguments));
            if (!subCommand.isDefault() && argumentsList.size() > 0) argumentsList.remove(0);

            // Check if the method only has a sender as parameter.
            if (subCommand.getParams().size() == 0 && argumentsList.size() == 0) {
                try {
                    method.invoke(subCommand.getCommandBase(), sender);
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof SyntaxError) {
                        return wrongUsage(sender, subCommand);
                    }

                    throw e.getCause();
                }
                return true;
            }

            // Checks if it is a default type command with just sender and args.
            if (subCommand.getParams().size() == 1
                    && String[].class.isAssignableFrom(subCommand.getParams().get(0))) {
                try {
                    method.invoke(subCommand.getCommandBase(), sender, arguments);
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof SyntaxError) {
                        return wrongUsage(sender, subCommand);
                    }

                    throw e.getCause();
                }
                return true;
            }

            // Checks for correct command usage.
            if (subCommand.getParams().size() != argumentsList.size() && !subCommand.hasOptional()) {

                if (!subCommand.isDefault() && subCommand.getParams().size() == 0)
                    return wrongUsage(sender, subCommand);

                if (!String[].class.isAssignableFrom(subCommand.getParams().get(subCommand.getParams().size() - 1)))
                    return wrongUsage(sender, subCommand);
            }

            // Creates a list of the params to send.
            final List<Object> invokeParams = new ArrayList<>();
            // Adds the sender as one of the params.
            invokeParams.add(sender);

            // Iterates through all the parameters to check them.
            for (int i = 0; i < subCommand.getParams().size(); i++) {
                final Class<?> parameter = subCommand.getParams().get(i);

                // Checks for optional parameter.
                if (subCommand.hasOptional()) {

                    if (argumentsList.size() > subCommand.getParams().size()) return wrongUsage(sender, subCommand);

                    if (argumentsList.size() < subCommand.getParams().size() - 1) return wrongUsage(sender, subCommand);

                    if (argumentsList.size() < subCommand.getParams().size()) argumentsList.add(null);

                }

                // checks if the parameters and arguments are valid
                if (subCommand.getParams().size() > argumentsList.size()) return wrongUsage(sender, subCommand);


                Object argument = argumentsList.get(i);

                // Checks if the current argument is annotated by @Values
                if (subCommand.getArgValue().contains(i + 1)) {
                    final String completionId = subCommand.getCompletions().get(i + 1);
                    // Checks if the value introduced is part of the completion
                    if (!completionHandler.getTypeResult(completionId, completionId).contains(argument))
                        argument = null;
                }

                // Checks for String[] args.
                if (parameter.equals(String[].class)) {
                    String[] args = new String[argumentsList.size() - i];

                    for (int j = 0; j < args.length; j++) {
                        args[j] = argumentsList.get(i + j);
                    }

                    argument = args;
                }

                final Object result = parameterHandler.getTypeResult(parameter, argument, subCommand, subCommand.getParameterNames().get(i));
                invokeParams.add(result);
            }

            // Calls the command method method.
            try {
                method.invoke(subCommand.getCommandBase(), invokeParams.toArray());
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof SyntaxError) {
                    return wrongUsage(sender, subCommand);
                }

                throw e.getCause();
            }
            subCommand.getCommandBase().clearArgs();

            return true;

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {

        // Checks if args is 1 so it sends the sub commands completion.
        if (args.length == 1) {
            List<String> commandNames = new ArrayList<>();

            final CommandData subCommand = getDefaultSubCommand();

            final List<String> subCmd = new ArrayList<>(commands.keySet());
            subCmd.remove("default");

            // removes commands that the player can't access.
            for (String subCmdName : commands.keySet()) {
                final CommandData subCmdData = commands.get(subCmdName);
                if (hideTab && subCmdData.hasPermissions() && !hasPermissions(sender, subCmdData))
                    subCmd.remove(subCmdName);
            }

            if (subCommand != null && subCommand.getCompletions().size() != 0) {
                String id = subCommand.getCompletions().get(1);
                Object inputClss = subCommand.getParams().get(0);

                // TODO range without thingy and also for double
                if (id.contains(":")) {
                    String[] values = id.split(":");
                    id = values[0];
                    inputClss = values[1];
                }

                subCmd.addAll(completionHandler.getTypeResult(id, inputClss));
            }

            // Checks if the typing command is empty.
            if (!"".equals(args[0])) {
                for (String commandName : subCmd) {
                    if (!commandName.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                    commandNames.add(commandName);
                }
            } else {
                commandNames = subCmd;
            }

            // Sorts the sub commands by alphabetical order.
            Collections.sort(commandNames);

            // Returns default tab completion if empty.
            if (commandNames.isEmpty()) return super.tabComplete(sender, alias, args);

            // The complete values.
            return commandNames;
        }

        final String subCommandArg = args[0];

        // Checks if it contains the sub command; Should always be true.
        if (!commands.containsKey(subCommandArg)) return super.tabComplete(sender, alias, args);

        final CommandData subCommand = commands.get(subCommandArg);

        // removes completion from commands that the player can't access.
        if (hideTab && subCommand.hasPermissions() && !hasPermissions(sender, subCommand))
            return super.tabComplete(sender, alias, args);

        final Method completionMethod = subCommand.getCompletionMethod();

        if (completionMethod != null) {
            try {
                List<String> argsList = new LinkedList<>(Arrays.asList(args));
                argsList.remove(subCommandArg);
                //noinspection unchecked
                return (List<String>) completionMethod.invoke(subCommand.getCommandBase(), argsList);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // Checks if the completion list has the current args position.
        if (!subCommand.getCompletions().containsKey(args.length - 1)) return super.tabComplete(sender, alias, args);

        // Gets the current ID.
        String id = subCommand.getCompletions().get(args.length - 1);

        List<String> completionList = new ArrayList<>();
        Object inputClss = subCommand.getParams().get(args.length - 2);

        // TODO range without thingy and also for double
        if (id.contains(":")) {
            String[] values = id.split(":");
            id = values[0];
            inputClss = values[1];
        }

        final String current = args[args.length - 1];

        // Checks if the typing completion is empty.
        if (!"".equals(current)) {
            for (String completion : completionHandler.getTypeResult(id, inputClss)) {
                if (!completion.toLowerCase().contains(current.toLowerCase())) continue;
                completionList.add(completion);
            }
        } else {
            completionList = new ArrayList<>(completionHandler.getTypeResult(id, inputClss));
        }

        // Sorts the completion content by alphabetical order.
        Collections.sort(completionList);

        // The complete values.
        return completionList;
    }

    /**
     * Sets whether you want to hide or not commands from tab completion if players don't have permission to use them.
     *
     * @param hideTab Hide or Not.
     */
    public void setHideTab(final boolean hideTab) {
        this.hideTab = hideTab;
    }

    /**
     * Gets the default method from the Command Data objects.
     *
     * @return The Command data of the default method if there is one.
     */
    private CommandData getDefaultSubCommand() {
        return commands.getOrDefault("default", null);
    }

    /**
     * Checks if the method is default.
     *
     * @param method     The method to check.
     * @param subCommand The subCommand object with the data.
     */
    private void checkDefault(final Method method, final CommandData subCommand) {
        // Checks if it is a default method.
        if (!method.isAnnotationPresent(Default.class)) return;

        subCommand.setDefault(true);
    }

    /**
     * Checks if the method has registered parameters or not.
     *
     * @param method     The method to check.
     * @param command    The commandBase object with the data.
     * @param subCommand The SubCommand object with the data.
     */
    private void checkRegisteredParams(final Method method, final CommandBase command, final CommandData subCommand) {
        // Checks if the parameters in class are registered.
        for (int i = 1; i < method.getParameterTypes().length; i++) {
            final Class<?> clss = method.getParameterTypes()[i];

            if (clss.equals(String[].class) && i != method.getParameterTypes().length - 1) {
                throw new InvalidParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " 'String[] args' have to be the last parameter if wants to be used!");
            }

            if (!clss.isEnum() && !this.parameterHandler.isRegisteredType(clss)) {
                throw new InvalidParamException("Method " + method.getName() + " in class " + command.getClass().getName() + " contains unregistered parameter types!");
            }

            subCommand.getParams().add(clss);
            subCommand.getParameterNames().add(method.getParameters()[i].getName());
        }
    }

    /**
     * Checks if the permission annotation is present.
     *
     * @param method     The method to check.
     * @param subCommand The commandBase object with the data.
     */
    private void checkPermission(final Method method, final CommandData subCommand) {
        // Checks if permission annotation is present.
        if (!method.isAnnotationPresent(Permission.class)) return;

        // Checks whether the command sender has the permission set in the annotation.
        for (final String permission : method.getAnnotation(Permission.class).value()) {
            subCommand.addPermission(permission);
        }

    }

    /**
     * Checks if the WrongUsage annotation is present.
     *
     * @param method     The method to check.
     * @param subCommand The commandBase object with the data.
     */
    private void checkWrongUsage(final Method method, final CommandData subCommand) {
        // Checks if WrongUsage annotation is present.
        if (!method.isAnnotationPresent(WrongUsage.class)) return;

        // Checks whether the command sender has the permission set in the annotation.
        subCommand.setWrongUsage(method.getAnnotation(WrongUsage.class).value());
    }

    /**
     * Checks if there is any completion on the parameters.
     *
     * @param method     The method to check.
     * @param command    The commandBase object with the data.
     * @param subCommand The SubCommand object with the data.
     */
    private void checkParamCompletion(final Method method, final CommandBase command, final CommandData subCommand) {
        // Checks for completion on the parameters.
        for (int i = 0; i < method.getParameters().length; i++) {
            final Parameter parameter = method.getParameters()[i];

            if (i == 0 && parameter.isAnnotationPresent(Completion.class))
                throw new InvalidParamAnnotationException("Method " + method.getName() + " in class " + command.getClass().getName() + " - First parameter of a command method cannot have Completion/Values annotation!");

            final String[] values;
            if (parameter.isAnnotationPresent(Completion.class))
                values = parameter.getAnnotation(Completion.class).value();
            else if (parameter.isAnnotationPresent(Values.class)) {
                values = new String[]{parameter.getAnnotation(Values.class).value()};
            } else continue;

            if (values.length != 1)
                throw new InvalidParamAnnotationException("Method " + method.getName() + " in class " + command.getClass().getName() + " - Parameter completion can only have one value!");

            if (!values[0].startsWith("#"))
                throw new InvalidCompletionIdException("Method " + method.getName() + " in class " + command.getClass().getName() + " - The completion ID must start with #!");

            if (completionHandler.isRegistered(values[0]))
                throw new InvalidCompletionIdException("Method " + method.getName() + " in class " + command.getClass().getName() + " - Unregistered completion ID '" + values[0] + "'!");

            subCommand.getCompletions().put(i, values[0]);
            if (parameter.isAnnotationPresent(Values.class)) subCommand.getArgValue().add(i);
        }
    }

    /**
     * Checks if there is any completion on the method.
     *
     * @param method     The method to check.
     * @param command    The commandBase object with the data.
     * @param subCommand The SubCommand object with the data.
     */
    private void checkMethodCompletion(final Method method, final CommandBase command, final CommandData subCommand) {
        // Checks for completion annotation in the method.
        if (!method.isAnnotationPresent(Completion.class)) return;

        final String[] completionValues = method.getAnnotation(Completion.class).value();

        for (int i = 0; i < completionValues.length; i++) {
            String id = completionValues[i];

            if (!id.startsWith("#"))
                throw new InvalidCompletionIdException("Method " + method.getName() + " in class " + command.getClass().getName() + " - The completion ID must start with #!");

            if (!completionHandler.isRegistered(id))
                throw new InvalidCompletionIdException("Method " + method.getName() + " in class " + command.getClass().getName() + " - Unregistered completion ID'" + id + "'!");

            subCommand.getCompletions().put(i + 1, id);
        }
    }

    private void checkCompletionMethod(final CommandBase command, final CommandData subCommand) {
        // Checks for completion annotation in the method.
        for (final Method method : command.getClass().getDeclaredMethods()) {
            // Checks for CompletionFor annotation
            if (!method.isAnnotationPresent(CompleteFor.class)) continue;

            // All the checks to make sure the complete for method returns String List
            if (!(method.getGenericReturnType() instanceof ParameterizedType)) return;

            final ParameterizedType parametrizedReturnType = (ParameterizedType) method.getGenericReturnType();

            if (parametrizedReturnType.getRawType() != List.class) return;
            if (parametrizedReturnType.getActualTypeArguments().length != 1) return;
            if (parametrizedReturnType.getActualTypeArguments()[0] != String.class) return;

            final String subCommandName = method.getAnnotation(CompleteFor.class).value();

            if (!subCommandName.equalsIgnoreCase(subCommand.getName())) continue;

            subCommand.setCompletionMethod(method);
        }
    }

    /**
     * Checks for aliases to be used.
     *
     * @param method     The method to check.
     * @param subCommand The SubCommand object with the data.
     */
    private void checkAlias(final Method method, final CommandData subCommand) {
        // Checks for aliases.
        if (!method.isAnnotationPresent(Alias.class)) return;

        // Iterates through the alias and add each as a normal sub command.
        for (String alias : method.getAnnotation(Alias.class).value()) {
            //noinspection UnnecessaryLocalVariable
            final CommandData aliasCD = subCommand;
            subCommand.setName(alias.toLowerCase());
            if (aliasCD.isDefault()) aliasCD.setDefault(false);
            commands.put(alias.toLowerCase(), subCommand);
        }

    }

    /**
     * Checks for optional parameter
     *
     * @param method     The method to check from
     * @param command    The command base class
     * @param subCommand The current sub command
     */
    private void checkOptionalParam(final Method method, final CommandBase command, final CommandData subCommand) {
        // Checks for completion on the parameters.
        for (int i = 0; i < method.getParameters().length; i++) {
            final Parameter parameter = method.getParameters()[i];

            if (i != method.getParameters().length - 1 && parameter.isAnnotationPresent(Optional.class))
                throw new InvalidParamAnnotationException("Method " + method.getName() + " in class " + command.getClass().getName() + " - Optional parameters can only be used as the last parameter of a method!");


            if (parameter.isAnnotationPresent(Optional.class)) subCommand.setOptional(true);
        }
    }

    /**
     * Checks if the player has one of the permissions listed
     *
     * @param sender     The command sender
     * @param subCommand The sub command class to check
     * @return If has or not one of the permissions
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermissions(final CommandSender sender, final CommandData subCommand) {
        for (final String permission : subCommand.getPermissions()) {
            if (sender.hasPermission(permission)) return true;
        }

        return false;
    }

    /**
     * Sends the wrong message to the sender
     *
     * @param sender     The sender
     * @param subCommand The current sub command to get info from
     * @return Returns true
     */
    private boolean wrongUsage(final CommandSender sender, final CommandData subCommand) {
        final String wrongMessage = subCommand.getWrongUsage();

        if (wrongMessage == null) {
            messageHandler.sendMessage("cmd.wrong.usage", sender);
            return true;
        }

        if (!wrongMessage.startsWith("#") || !messageHandler.hasId(wrongMessage)) {
            messageHandler.sendMessage("cmd.wrong.usage", sender);
            sender.sendMessage(color(subCommand.getWrongUsage()));
            return true;
        }

        messageHandler.sendMessage(wrongMessage, sender);
        return true;
    }

    /**
     * Sends the unknown message to the sender
     *
     * @param sender The sender
     * @return Returns true
     */
    private boolean unknownCommand(final CommandSender sender) {
        messageHandler.sendMessage("cmd.no.exists", sender);
        return true;
    }

    /**
     * Sends the no permission message to the sender
     *
     * @param sender The sender
     * @return Returns true
     */
    private boolean noPermission(final CommandSender sender) {
        messageHandler.sendMessage("cmd.no.permission", sender);
        return true;
    }

    /**
     * Sends the no console allowed message to the sender
     *
     * @param sender The sender
     * @return Returns true
     */
    private boolean noConsole(final CommandSender sender) {
        messageHandler.sendMessage("cmd.no.console", sender);
        return true;
    }
}
