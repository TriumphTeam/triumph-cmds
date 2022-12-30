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
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.internal.ArgumentGroup;
import dev.triumphteam.cmd.core.command.ExecutableCommand;
import dev.triumphteam.cmd.core.command.ParentSubCommand;
import dev.triumphteam.cmd.core.command.SubCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@SuppressWarnings("unchecked")
public abstract class AbstractRootCommandProcessor<S> implements CommandProcessor {

    private final BaseCommand baseCommand;

    private final String name;
    private final List<String> alias;
    private final String description;

    private final CommandExtensions<?, S> commandExtensions;
    private final RegistryContainer<S> registryContainer;

    public AbstractRootCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull CommandExtensions<?, S> commandExtensions
    ) {
        this.baseCommand = baseCommand;

        this.name = nameOf();
        this.alias = aliasOf();
        this.description = descriptionOf();

        this.registryContainer = registryContainer;
        this.commandExtensions = commandExtensions;
    }

    public String getName() {
        return name;
    }

    public List<String> getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    @Contract(" -> new")
    @Override
    public @NotNull CommandMeta createMeta() {
        final CommandMeta.Builder meta = new CommandMeta.Builder(null);
        // Process all the class annotations
        final Class<?> klass = baseCommand.getClass();
        processAnnotations(commandExtensions, klass, ProcessorTarget.ROOT_COMMAND, meta);
        processCommandMeta(commandExtensions, klass, ProcessorTarget.PARENT_COMMAND, meta);
        // Return modified meta
        return meta.build();
    }

    public @NotNull List<ExecutableCommand<S>> commands(final @NotNull CommandMeta parentMeta) {
        final Class<? extends BaseCommand> klass = baseCommand.getClass();

        final List<ExecutableCommand<S>> subCommands = new ArrayList<>();
        subCommands.addAll(methodCommands(parentMeta, klass.getDeclaredMethods()));
        subCommands.addAll(classCommands(parentMeta, klass.getDeclaredClasses()));

        return subCommands;
    }

    private @NotNull List<ExecutableCommand<S>> methodCommands(
            final @NotNull CommandMeta parentMeta,
            final @NotNull Method[] methods
    ) {
        final List<ExecutableCommand<S>> commands = new ArrayList<>();
        for (final Method method : methods) {
            // Ignore non-public methods
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final SubCommandProcessor<S> processor = new SubCommandProcessor<>(
                    name,
                    baseCommand,
                    method,
                    registryContainer,
                    commandExtensions,
                    parentMeta
            );

            // Not a command, ignore the method
            if (processor.getName() == null) continue;

            // Add new command
            commands.add(new SubCommand<>(baseCommand, method, processor));
        }

        return commands;
    }

    private @NotNull List<ExecutableCommand<S>> classCommands(
            final @NotNull CommandMeta parentMeta,
            final @NotNull Class<?>[] classes
    ) {
        final List<ExecutableCommand<S>> commands = new ArrayList<>();
        for (final Class<?> klass : classes) {
            // Ignore non-public methods
            if (!Modifier.isPublic(klass.getModifiers())) continue;

            final ParentCommandProcessor<S> processor = new ParentCommandProcessor<>(
                    name,
                    baseCommand,
                    klass,
                    registryContainer,
                    commandExtensions,
                    parentMeta
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

            final InternalArgument<S, ?> argument;
            if (!hasArgument) argument = null;
            else {
                if (!Command.DEFAULT_CMD_NAME.equals(processor.getName())) {
                    throw new CommandRegistrationException("Inner command class with argument must not have a name", klass);
                }

                final Parameter parameter = isStatic ? parameters[0] : parameters[1];
                argument = processor.createArgument(
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

            final ParentSubCommand<S> parent = new ParentSubCommand<>(baseCommand, constructor, isStatic, (StringInternalArgument<S>) argument, processor);

            // Add children commands to parent
            methodCommands(parent.getMeta(), klass.getDeclaredMethods()).forEach(it -> parent.addSubCommand(it, false));
            classCommands(parent.getMeta(), klass.getDeclaredClasses()).forEach(it -> parent.addSubCommand(it, false));

            // Add parent command to main list
            commands.add(parent);
        }

        return commands;
    }

    private @NotNull String nameOf() {
        final Class<? extends @NotNull BaseCommand> commandClass = baseCommand.getClass();
        final dev.triumphteam.cmd.core.annotations.Command commandAnnotation = commandClass.getAnnotation(dev.triumphteam.cmd.core.annotations.Command.class);

        final String name;
        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or \"@" + dev.triumphteam.cmd.core.annotations.Command.class.getSimpleName() + "\" annotation missing", baseCommand.getClass());
            }

            name = commandName;
        } else {
            name = commandAnnotation.value();
        }

        if (name.isEmpty() || name.equals(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME)) {
            throw new CommandRegistrationException("Command name must not be empty", baseCommand.getClass());
        }

        return name;
    }

    private @NotNull List<String> aliasOf() {
        final dev.triumphteam.cmd.core.annotations.Command commandAnnotation = baseCommand.getClass().getAnnotation(dev.triumphteam.cmd.core.annotations.Command.class);
        return commandAnnotation == null ? baseCommand.getAlias() : Arrays.asList(commandAnnotation.alias());
    }

    private @NotNull String descriptionOf() {
        final Description commandAnnotation = baseCommand.getClass().getAnnotation(Description.class);
        return commandAnnotation == null ? baseCommand.getDescription() : commandAnnotation.value();
    }
}
