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

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.AnnotatedCommand;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentGroup;
import dev.triumphteam.cmd.core.command.InternalBranchCommand;
import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@SuppressWarnings("unchecked")
public class RootCommandProcessor<D, S, ST> implements CommandProcessor<D, S, ST> {

    private final Object invocationInstance;

    private final String name;
    private final Syntax syntax;
    private final List<String> aliases;

    private final CommandOptions<?, ?, D, S, ST> commandOptions;
    private final RegistryContainer<D, S, ST> registryContainer;

    public RootCommandProcessor(
            final @NotNull Object invocationInstance,
            final @NotNull RegistryContainer<D, S, ST> registryContainer,
            final @NotNull CommandOptions<?, ?, D, S, ST> commandOptions
    ) {
        this.invocationInstance = invocationInstance;

        this.name = nameOf();
        this.aliases = aliasesOf();

        this.registryContainer = registryContainer;
        this.commandOptions = commandOptions;

        this.syntax = invocationInstance.getClass().getAnnotation(Syntax.class);
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull List<String> getAliases() {
        return aliases;
    }

    @Override
    public @NotNull CommandOptions<?, ?, D, S, ST> getCommandOptions() {
        return commandOptions;
    }

    @Override
    public @NotNull RegistryContainer<D, S, ST> getRegistryContainer() {
        return registryContainer;
    }

    @Override
    public @Nullable Syntax getSyntaxAnnotation() {
        return syntax;
    }

    @Override
    public @NotNull AnnotatedElement getAnnotatedElement() {
        return invocationInstance.getClass();
    }

    @Override
    public @NotNull CommandMeta createMeta(final @NotNull Settings.@NotNull Builder<D, S> settingsBuilder) {
        final CommandMeta.Builder meta = new CommandMeta.Builder(null);

        // Defaults
        meta.add(MetaKey.NAME, getName());
        meta.add(MetaKey.DESCRIPTION, descriptionOf());

        // Process all the class annotations
        final Class<?> klass = invocationInstance.getClass();
        processAnnotations(commandOptions.getCommandExtensions(), klass, ProcessorTarget.ROOT_COMMAND, meta);
        processCommandMeta(
                commandOptions.getCommandExtensions(),
                klass,
                ProcessorTarget.PARENT_COMMAND,
                meta,
                settingsBuilder
        );

        // Return modified meta
        return meta.build();
    }

    public @NotNull List<InternalCommand<D, S, ST>> commands(final @NotNull InternalCommand<D, S, ST> parentCommand) {
        final Class<?> klass = invocationInstance.getClass();

        final List<InternalCommand<D, S, ST>> subCommands = new ArrayList<>();
        subCommands.addAll(methodCommands(parentCommand, klass.getDeclaredMethods()));
        subCommands.addAll(classCommands(parentCommand, klass.getDeclaredClasses()));

        return subCommands;
    }

    private @NotNull List<InternalCommand<D, S, ST>> methodCommands(
            final @NotNull InternalCommand<D, S, ST> parentCommand,
            final @NotNull Method[] methods
    ) {
        final List<InternalCommand<D, S, ST>> commands = new ArrayList<>();
        for (final Method method : methods) {
            // Ignore non-public methods
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final LeafCommandProcessor<D, S, ST> processor = new LeafCommandProcessor<>(
                    invocationInstance,
                    method,
                    registryContainer,
                    commandOptions,
                    parentCommand.getMeta()
            );

            // Not a command, ignore the method
            if (processor.getName() == null) continue;

            // Add a new command
            commands.add(new InternalLeafCommand<>(invocationInstance, method, processor, parentCommand));
        }

        return commands;
    }

    private @NotNull List<InternalCommand<D, S, ST>> classCommands(
            final @NotNull InternalCommand<D, S, ST> parentCommand,
            final @NotNull Class<?>[] classes
    ) {
        final List<InternalCommand<D, S, ST>> commands = new ArrayList<>();
        for (final Class<?> klass : classes) {
            // Ignore non-public methods
            if (!Modifier.isPublic(klass.getModifiers())) continue;

            final BranchCommandProcessor<D, S, ST> processor = new BranchCommandProcessor<>(
                    invocationInstance,
                    klass,
                    registryContainer,
                    commandOptions,
                    parentCommand.getMeta()
            );

            // Not a command, ignore the method
            if (processor.getName() == null) continue;

            // Validation for allowed constructor
            final Constructor<?>[] constructors = klass.getConstructors();
            if (constructors.length != 1) {
                throw new CommandRegistrationException("Inner command class can only have a single constructor, " + constructors.length + " found", klass);
            }

            // Validation for allowed arguments
            final Constructor<?> constructor = constructors[0];
            final Parameter[] parameters = constructor.getParameters();

            final boolean isStatic = Modifier.isStatic(klass.getModifiers());
            final int arguments = (isStatic ? parameters.length : parameters.length - 1);
            final boolean hasArgument = arguments != 0;

            if (arguments > 1) {
                throw new CommandRegistrationException("Inner command class can only have a maximum of 1 argument, " + arguments + " found", klass);
            }

            final InternalArgument<S, ST> argument;
            if (!hasArgument) argument = null;
            else {
                if (!InternalCommand.PARENT_CMD_WITH_ARGS_NAME.equals(processor.getName())) {
                    throw new CommandRegistrationException("Inner command class with argument must not have a name", klass);
                }

                final Parameter parameter = isStatic ? parameters[0] : parameters[1];

                final CommandMeta.Builder meta = new CommandMeta.Builder(null);
                processAnnotations(getCommandOptions().getCommandExtensions(), parameter, ProcessorTarget.ARGUMENT, meta);

                argument = processor.argumentFromParameter(
                        meta.build(),
                        parameter,
                        emptyList(),
                        emptyMap(),
                        ArgumentGroup.flags(emptyList()),
                        ArgumentGroup.named(emptyList()),
                        0
                );

                if (!(argument instanceof StringInternalArgument)) {
                    throw new CommandRegistrationException("Inner command class with argument must not be limitless, only single string argument is allowed", klass);
                }
            }

            final InternalBranchCommand<D, S, ST> parent = new InternalBranchCommand<>(
                    invocationInstance,
                    constructor,
                    isStatic,
                    (StringInternalArgument<S, ST>) argument,
                    processor,
                    parentCommand
            );

            // Add children commands to parent
            parent.addCommands(invocationInstance, methodCommands(parent, klass.getDeclaredMethods()));
            parent.addCommands(invocationInstance, classCommands(parent, klass.getDeclaredClasses()));

            // Add parent command to main list
            commands.add(parent);
        }

        return commands;
    }

    private @NotNull String nameOf() {
        final Class<?> commandClass = invocationInstance.getClass();
        final Command commandAnnotation = commandClass.getAnnotation(Command.class);

        String name = null;
        if (commandAnnotation != null) {
            name = commandAnnotation.value();
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            name = ((AnnotatedCommand) invocationInstance).getCommand();
        }

        if (name == null) {
            throw new CommandRegistrationException("No \"@" + InternalCommand.class.getSimpleName() + "\" annotation found or class doesn't extend \"" + AnnotatedCommand.class.getSimpleName() + "\"", invocationInstance.getClass());
        }

        if (name.isEmpty() || name.equals(InternalCommand.DEFAULT_CMD_NAME)) {
            throw new CommandRegistrationException("Command name must not be empty", invocationInstance.getClass());
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
    }

    private @NotNull List<String> aliasesOf() {
        final Class<?> commandClass = invocationInstance.getClass();
        final Command commandAnnotation = commandClass.getAnnotation(Command.class);

        List<String> aliases = null;
        if (commandAnnotation != null) {
            aliases = Arrays.asList(commandAnnotation.alias());
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            aliases = ((AnnotatedCommand) invocationInstance).getAlias();
        }

        return aliases == null ? emptyList() : aliases.stream()
                .map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name))
                .collect(Collectors.toList());
    }

    private @NotNull String descriptionOf() {
        final Class<?> commandClass = invocationInstance.getClass();
        final Description descriptionAnnotation = commandClass.getAnnotation(Description.class);

        String description = "";
        if (descriptionAnnotation != null) {
            description = descriptionAnnotation.value();
        } else if (AnnotatedCommand.class.isAssignableFrom(commandClass)) {
            description = ((AnnotatedCommand) invocationInstance).getDescription();
        }

        return description;
    }
}
