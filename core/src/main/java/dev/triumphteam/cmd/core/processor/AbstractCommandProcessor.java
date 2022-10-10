/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.subcommand.SubCommand;
import dev.triumphteam.cmd.core.subcommand.invoker.Invoker;
import dev.triumphteam.cmd.core.subcommand.invoker.MethodInvoker;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstracts most of the "extracting" from command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> Sender type
 */
public abstract class AbstractCommandProcessor<DS, S, SC extends SubCommand<S>, P extends AbstractSubCommandProcessor<S>> {

    private String name;
    // TODO: 11/28/2021 Add better default description
    private String description = "No description provided.";
    private final List<String> alias = new ArrayList<>();
    private final Map<String, SC> subCommands = new HashMap<>();
    private final Map<String, SC> subCommandsAlias = new HashMap<>();

    private final Class<? extends BaseCommand> commandClass;
    private final Supplier<BaseCommand> instanceSupplier;
    private final RegistryContainer<S> registryContainer;
    private final SenderMapper<DS, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    protected AbstractCommandProcessor(
            final @NotNull Class<? extends BaseCommand> commandClass,
            final @NotNull Supplier<BaseCommand> instanceSupplier,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull SenderMapper<DS, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.commandClass = commandClass;
        this.instanceSupplier = instanceSupplier;
        this.registryContainer = registryContainer;
        this.senderMapper = senderMapper;
        this.senderValidator = senderValidator;
        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;

        extractCommandNames();
        extractDescription();
    }

    // TODO: Comments
    public void addSubCommands(final @NotNull dev.triumphteam.cmd.core.Command<S, SC> command) {
        // Method sub commands
        collectMethodSubCommands(command, commandClass, method -> new MethodInvoker(instanceSupplier, method));

        // Classes sub commands
        for (final Class<?> klass : commandClass.getDeclaredClasses()) {
            final List<Constructor<?>> constructors = Arrays.asList(klass.getDeclaredConstructors());

            if (constructors.size() != 1) {
                throw new SubCommandRegistrationException("TODO constructs", null, null);
            }

            final Constructor<?> constructor = constructors.get(0);

            final boolean isStatic = Modifier.isStatic(klass.getModifiers());
            final int argsSize = isStatic ? constructor.getParameterCount() : constructor.getParameterCount() - 1;

            if (argsSize > 1) {
                throw new SubCommandRegistrationException("TODO params", null, null);
            }

            final boolean hasArg = argsSize > 0;

            final P processor = createSubProcessor(klass);
            final String subCommandName = processor.getName();
            // Not a command
            if (subCommandName == null) continue;
            // If the name is empty and there is no arguments
            if (subCommandName.isEmpty() && !hasArg) {
                throw new SubCommandRegistrationException(
                        "@" + Command.class.getSimpleName() + " name must not be empty on a class unless it has an argument.",
                        klass,
                        commandClass
                );
            }

        }
    }

    private void collectMethodSubCommands(
            final @NotNull dev.triumphteam.cmd.core.Command<S, SC> command,
            final @NotNull Class<?> klass,
            final @NotNull Function<Method, Invoker> invokerFunction
    ) {
        // Method sub commands
        for (final Method method : klass.getDeclaredMethods()) {
            // TODO: ALLOW PRIVATE
            if (Modifier.isPrivate(method.getModifiers())) continue;

            final Invoker invoker = invokerFunction.apply(method);

            final P processor = createSubProcessor(method);
            final String subCommandName = processor.getName();
            // Not a command
            if (subCommandName == null) continue;

            if (subCommandName.isEmpty()) {
                throw new SubCommandRegistrationException(
                        "@" + dev.triumphteam.cmd.core.annotation.SubCommand.class.getSimpleName() + " name must not be empty",
                        method,
                        klass
                );
            }

            final ExecutionProvider executionProvider = processor.isAsync() ? asyncExecutionProvider : syncExecutionProvider;

            final SC subCommand = createSubCommand(processor, executionProvider);
            command.addSubCommand(subCommandName, subCommand);

            processor.getAlias().forEach(alias -> command.addSubCommandAlias(alias, subCommand));
        }
    }

    protected abstract @NotNull P createSubProcessor(final @NotNull AnnotatedElement method);

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

    // TODO: Comments
    public @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    /**
     * Gets the {@link SenderMapper}.
     *
     * @return The {@link SenderMapper}.
     */
    public @NotNull SenderMapper<DS, S> getSenderMapper() {
        return senderMapper;
    }

    // TODO: 2/4/2022 comments
    public @NotNull SenderValidator<S> getSenderValidator() {
        return senderValidator;
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
        final Command commandAnnotation = commandClass.getAnnotation(Command.class);

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
