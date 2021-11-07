/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
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
package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstracts most of the "extracting" from command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> Sender type
 */
public abstract class AbstractCommandProcessor<S> {

    private final Class<?> annotatedClass;

    private String name;
    private final List<String> alias = new ArrayList<>();

    private final BaseCommand baseCommand;
    private final ArgumentRegistry<S> argumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final MessageRegistry<S> messageRegistry;

    protected AbstractCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry
    ) {
        this.baseCommand = baseCommand;
        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
        this.messageRegistry = messageRegistry;

        this.annotatedClass = extractAnnotationClass();
        extractCommandNames();
    }

    /**
     * Used for the child factories to get the command name.
     *
     * @return The command name.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Used for the child factories to get a {@link List<String>} with the command's alias.
     *
     * @return The command alias.
     */
    @NotNull
    public List<String> getAlias() {
        return alias;
    }

    // TODO: 11/6/2021 Comments
    @NotNull
    public BaseCommand getBaseCommand() {
        return baseCommand;
    }

    // TODO: 11/6/2021 Comments
    @NotNull
    public ArgumentRegistry<S> getArgumentRegistry() {
        return argumentRegistry;
    }

    // TODO: 11/6/2021 Comments
    @NotNull
    public RequirementRegistry<S> getRequirementRegistry() {
        return requirementRegistry;
    }

    // TODO: 11/6/2021 Comments
    @NotNull
    public MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    // TODO: 11/7/2021 Comments
    protected Class<?> getAnnotatedClass() {
        return annotatedClass;
    }

    // TODO: 11/7/2021 Comments
    private Class<?> extractAnnotationClass() {
        final Class<? extends BaseCommand> commandClass = baseCommand.getClass();
        final Class<?> parent = commandClass.getSuperclass();
        return parent != BaseCommand.class ? parent : commandClass;
    }

    /**
     * Helper method for getting the command names from the command annotation.
     */
    private void extractCommandNames() {
        final Command commandAnnotation = AnnotationUtil.getAnnotation(annotatedClass, Command.class);

        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or \"@" + Command.class.getSimpleName() + "\" annotation missing", baseCommand.getClass());
            }

            name = commandName;
            alias.addAll(baseCommand.getAlias());
        } else {
            name = commandAnnotation.value();
            Collections.addAll(alias, commandAnnotation.alias());
        }

        alias.addAll(baseCommand.getAlias());

        if (name.isEmpty()) {
            throw new CommandRegistrationException("Command name must not be empty", baseCommand.getClass());
        }
    }

}
