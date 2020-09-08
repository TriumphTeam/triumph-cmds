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

package me.mattstudios.mfcmd.base;


import me.mattstudios.mfcmd.base.annotations.Completion;
import me.mattstudios.mfcmd.base.annotations.Default;
import me.mattstudios.mfcmd.base.annotations.Requirement;
import me.mattstudios.mfcmd.base.annotations.SubCommand;
import me.mattstudios.mfcmd.base.components.CommandData;
import me.mattstudios.mfcmd.base.components.util.Constant;
import me.mattstudios.mfcmd.base.exceptions.MfException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public final class CommandHandler {

    @NotNull
    private final Map<String, CommandData> commands = new LinkedHashMap<>();

    @NotNull
    private final ParameterHandler parameterHandler;
    @NotNull
    private final RequirementHandler<?> requirementHandler;
    @Nullable
    private final CompletionHandler completionHandler;

    public CommandHandler(
            @NotNull final ParameterHandler parameterHandler,
            @NotNull final RequirementHandler<?> requirementHandler,
            @Nullable final CompletionHandler completionHandler
    ) {
        this.parameterHandler = parameterHandler;
        this.requirementHandler = requirementHandler;
        this.completionHandler = completionHandler;
    }

    /**
     * Adds a subcommand to the command
     *
     * @param method      The subcommand method
     * @param command     The parent {@link CommandBase}
     * @param commandData The current {@link CommandData}
     */
    public void addSubCommand(@NotNull final Method method, @NotNull final CommandBase command, @NotNull final CommandData commandData) {
        // Checks the subcommand name
        final String subCommandName = getCommandName(method);
        commandData.setName(subCommandName);

        checkRequirements(method, commandData);

        checkParameters(method, commandData, getMethodCompletion(method));


        commands.put("commandName", commandData);

    }

    private void checkParameters(final Method method, final CommandData commandData, final List<String> methodCompletion) {
        final ListIterator<Parameter> iterator = Arrays.asList(method.getParameters()).listIterator();

        while (iterator.hasNext()) {
            final int index = iterator.nextIndex();
            final Parameter parameter = iterator.next();

            if (parameter.getType().equals(String[].class) && !iterator.hasNext()) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " 'String[] args' have to be the last parameter if wants to be used!");
            }

            if (!parameterHandler.isRegisteredType(parameter.getType())) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " contains unregistered parameter types!");
            }

            String currentCompletion = null;
            if (index > 0 && index - 1 < methodCompletion.size()) {
                currentCompletion = methodCompletion.get(index - 1);
            }

            System.out.println(currentCompletion);

            System.out.println(parameter.getType().getName() + " - " + index);
            System.out.println(methodCompletion);


            //commandData.addParameter(parameter);
        }
    }

    private List<String> getMethodCompletion(@NotNull final Method method) {
        if (completionHandler == null) return Collections.emptyList();
        if (!method.isAnnotationPresent(Completion.class)) return Collections.emptyList();

        final String[] completionValues = method.getAnnotation(Completion.class).value();

        for (final String id : completionValues) {
            if (!id.startsWith("#")) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " - The completion ID must start with #!");
            }

            if (!completionHandler.isRegistered(id)) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " - Unregistered completion ID'" + id + "'!");
            }
        }

        return Arrays.asList(completionValues);
    }

    /**
     * Checks for requirement annotation
     *
     * @param method      The method with the annotation
     * @param commandData The current {@link CommandData}
     */
    private void checkRequirements(@NotNull final Method method, @NotNull final CommandData commandData) {
        if (method.isAnnotationPresent(Requirement.class)) {
            final String requirementId = method.getAnnotation(Requirement.class).value();

            if (!requirementId.startsWith("#")) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " - The requirement ID must start with #!");
            }

            if (!requirementHandler.isRegistered(requirementId)) {
                throw new MfException("Method " + method.getName() + " in class " + method.getClass().getName() + " - The ID entered in the requirement doesn't exist!");
            }

            commandData.addRequirement(requirementId);
        }
    }

    @NotNull
    @Contract(pure = true)
    private String getCommandName(final Method method) {
        if (method.isAnnotationPresent(Default.class)) return Constant.DEFAULT;
        else return method.getAnnotation(SubCommand.class).value();
    }

}
