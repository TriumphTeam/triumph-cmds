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
package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import dev.triumphteam.cmd.core.processor.ParentCommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A parent sub command is basically a holder of other sub commands.
 * This normally represents an inner class of a main command.
 * It can contain arguments which will turn it into an argument-as-subcommand type.
 *
 * @param <S> The sender type to be used.
 */
public class ParentSubCommand<S> implements ParentCommand<S>, ExecutableCommand<S> {

    private final Map<String, ExecutableCommand<S>> commands = new HashMap<>();
    private final Map<String, ExecutableCommand<S>> commandAliases = new HashMap<>();

    private final String name;
    private final String syntax;
    private final CommandMeta meta;

    private final Object invocationInstance;
    private final Constructor<?> constructor;
    private final boolean isStatic;
    private final StringInternalArgument<S> argument;
    private final boolean hasArgument;
    private final MessageRegistry<S> messageRegistry;

    // Single parent command with argument
    private ExecutableCommand<S> parentCommandWithArgument;

    public ParentSubCommand(
            final @NotNull Object invocationInstance,
            final @NotNull Constructor<?> constructor,
            final boolean isStatic,
            final @Nullable StringInternalArgument<S> argument,
            final @NotNull ParentCommandProcessor<S> processor,
            final @NotNull Command parentCommand
    ) {
        this.invocationInstance = invocationInstance;
        this.constructor = constructor;
        this.isStatic = isStatic;
        this.argument = argument;
        this.hasArgument = argument != null;

        this.name = processor.getName();
        this.meta = processor.createMeta();
        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();

        this.syntax = createSyntax(parentCommand, processor);
    }

    @Override
    public void addSubCommand(final @NotNull ExecutableCommand<S> subCommand, final boolean isAlias) {
        // If it's a parent command with argument we add it
        if (subCommand instanceof ParentSubCommand && subCommand.hasArguments()) {
            if (parentCommandWithArgument != null) {
                throw new CommandRegistrationException("Only one inner command with argument is allowed per command", invocationInstance.getClass());
            }

            parentCommandWithArgument = subCommand;
            return;
        }

        // Normal commands are added here
        commands.put(subCommand.getName(), subCommand);
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @NotNull String command,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull List<String> commandPath,
            final @NotNull List<String> arguments
    ) throws Throwable {
        final int argumentSize = arguments.size();

        final String commandName = nameFromArguments(arguments);
        final ExecutableCommand<S> subCommand = getSubCommand(commandName, argumentSize);

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(meta, commandName));
            return;
        }

        commandPath.add(subCommand.getName());

        final Object instance;

        if (hasArgument) {
            final @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> result =
                    argument.resolve(sender, command);

            if (result instanceof Result.Failure) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((Result.Failure<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result)
                                .getFail()
                                .apply(meta, syntax)
                );
                return;
            }

            if (!(result instanceof Result.Success)) {
                throw new CommandExecutionException("An error occurred resolving arguments", "", name);
            }

            instance = createInstanceWithArgument(
                    instanceSupplier, ((Result.Success<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result).getValue()
            );
        } else {
            instance = createInstance(instanceSupplier);
        }

        subCommand.execute(
                sender, commandName,
                () -> instance,
                commandPath,
                !subCommand.isDefault() && !arguments.isEmpty() ? arguments.subList(1, arguments.size()) : arguments
        );
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @return An instance of this command for execution.
     */
    private @NotNull Object createInstance(
            final @Nullable Supplier<Object> instanceSupplier
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!isStatic) {
            return constructor.newInstance(instanceSupplier == null ? invocationInstance : instanceSupplier.get());
        }

        return constructor.newInstance();
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @param argumentValue    The argument value.
     * @return An instance of this command for execution.
     */
    private @NotNull Object createInstanceWithArgument(
            final @Nullable Supplier<Object> instanceSupplier,
            final @Nullable Object argumentValue
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Non-static classes required parent instance
        if (!isStatic) {
            return constructor.newInstance(instanceSupplier == null ? invocationInstance : instanceSupplier.get(), argumentValue);
        }

        return constructor.newInstance(argumentValue);
    }

    private @NotNull String createSyntax(final @NotNull Command parentCommand, final @NotNull CommandProcessor processor) {
        final Syntax syntaxAnnotation = processor.getSyntaxAnnotation();
        if (syntaxAnnotation != null) return syntaxAnnotation.value();

        final StringBuilder builder = new StringBuilder();
        builder.append(parentCommand.getSyntax()).append(" ");

        if (hasArgument) builder.append("<").append(argument.getName()).append(">");
        else builder.append(name);

        return builder.toString();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getSyntax() {
        return syntax;
    }

    @Override
    public @NotNull Object getInvocationInstance() {
        return invocationInstance;
    }

    @Override
    public @Nullable ExecutableCommand<S> getParentCommandWithArgument() {
        return parentCommandWithArgument;
    }

    @Override
    public boolean hasArguments() {
        return argument != null;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
