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
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstracts most of the "extracting" from command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> Sender type
 */
public abstract class AbstractCommandProcessor<SD, S, SC extends SubCommand<S>, P extends AbstractSubCommandProcessor<S>> {

    private String name;
    // TODO: 11/28/2021 Add better default description
    private String description = "No description provided.";
    private final List<String> alias = new ArrayList<>();
    private final Map<String, SC> subCommands = new HashMap<>();
    private final Map<String, SC> subCommandsAlias = new HashMap<>();

    private final BaseCommand baseCommand;
    private final RegistryContainer<S> registryContainer;
    private final SenderMapper<SD, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    protected AbstractCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull SenderMapper<SD, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.baseCommand = baseCommand;
        this.registryContainer = registryContainer;
        this.senderMapper = senderMapper;
        this.senderValidator = senderValidator;
        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;

        extractCommandNames();
        extractDescription();
    }

    // TODO: Comments
    public void addSubCommands(final dev.triumphteam.cmd.core.@NotNull Command<S, SC> command) {
        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) continue;

            final P processor = createProcessor(method);
            final String subCommandName = processor.getName();
            if (subCommandName == null) continue;

            final ExecutionProvider executionProvider = processor.isAsync() ? asyncExecutionProvider : syncExecutionProvider;

            final SC subCommand = createSubCommand(processor, executionProvider);
            command.addSubCommand(subCommandName, subCommand);

            processor.getAlias().forEach(alias -> command.addSubCommandAlias(alias, subCommand));
        }
    }

    protected abstract @NotNull P createProcessor(final @NotNull Method method);

    protected abstract @NotNull SC createSubCommand(final @NotNull P processor, final @NotNull ExecutionProvider executionProvider);

    /**
     * Used for the child processors to get the command name.
     *
     * @return The command name.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Used for the child processors to get a {@link List<String>} with the command's alias.
     *
     * @return The command alias.
     */
    public @NotNull List<@NotNull String> getAlias() {
        return alias;
    }

    /**
     * Gets the {@link BaseCommand} which is needed to invoke the command later.
     *
     * @return The {@link BaseCommand}.
     */
    public @NotNull BaseCommand getBaseCommand() {
        return baseCommand;
    }

    // TODO: Comments
    public @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    /**
     * Gets the {@link SenderMapper}.
     *
     * @return The {@link SenderMapper}.
     */
    public @NotNull SenderMapper<SD, S> getSenderMapper() {
        return senderMapper;
    }

    // TODO: 2/4/2022 comments
    public @NotNull SenderValidator<S> getSenderValidator() {
        return senderValidator;
    }

    public @NotNull Map<@NotNull String, SC> getSubCommands() {
        return subCommands;
    }

    public @NotNull Map<@NotNull String, SC> getSubCommandsAlias() {
        return subCommandsAlias;
    }

    public @NotNull ExecutionProvider getSyncExecutionProvider() {
        return syncExecutionProvider;
    }

    public @NotNull ExecutionProvider getAsyncExecutionProvider() {
        return asyncExecutionProvider;
    }

    /**
     * gets the Description of the SubCommand.
     *
     * @return either the extracted Description or the default one.
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Helper method for getting the command names from the command annotation.
     */
    private void extractCommandNames() {
        final Command commandAnnotation = baseCommand.getClass().getAnnotation(Command.class);

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

    /**
     * Extracts the {@link Description} Annotation from the annotatedClass.
     */
    private void extractDescription() {
        final Description description = baseCommand.getClass().getAnnotation(Description.class);
        if (description == null) return;
        this.description = description.value();
    }

}
