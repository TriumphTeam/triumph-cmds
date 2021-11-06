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
package dev.triumphteam.cmd.core.factory;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import org.jetbrains.annotations.Contract;
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
 * @param <C> The command type.
 */
public abstract class AbstractCommandFactory<S, C extends Command> {

    private String name;
    private final List<String> alias = new ArrayList<>();

    private final BaseCommand baseCommand;
    private final ArgumentRegistry<S> argumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final MessageRegistry<S> messageRegistry;

    protected AbstractCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry
    ) {
        this.baseCommand = baseCommand;
        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
        this.messageRegistry = messageRegistry;

        extractCommandNames(baseCommand);
    }

    /**
     * Abstract method so children can handle the return of the new {@link Command}.
     *
     * @return A {@link Command} implementation.
     */
    @NotNull
    @Contract(" -> new")
    public abstract C create();

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

    /**
     * Helper method for getting the command names from the command annotation.
     *
     * @param baseCommand The {@link BaseCommand} instance.
     * @throws CommandRegistrationException In case something goes wrong should throw exception.
     */
    private void extractCommandNames(@NotNull final BaseCommand baseCommand) throws CommandRegistrationException {
        final Class<? extends BaseCommand> commandClass = baseCommand.getClass();
        final dev.triumphteam.cmd.core.annotation.Command commandAnnotation = AnnotationUtil.getAnnotation(commandClass, dev.triumphteam.cmd.core.annotation.Command.class);

        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or \"@" + Command.class.getSimpleName() + "\" annotation missing", commandClass);
            }

            this.name = commandName;
            this.alias.addAll(baseCommand.getAlias());
        } else {
            this.name = commandAnnotation.value();
            Collections.addAll(this.alias, commandAnnotation.alias());
        }

        this.alias.addAll(baseCommand.getAlias());

        if (this.name.isEmpty()) {
            throw new CommandRegistrationException("Command name must not be empty", commandClass);
        }
    }

}
