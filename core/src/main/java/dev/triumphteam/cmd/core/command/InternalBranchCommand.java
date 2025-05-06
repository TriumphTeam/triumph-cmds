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
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.BranchCommandProcessor;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

/**
 * A parent sub command is basically a holder of other sub commands.
 * This normally represents an inner class of a main command.
 * It can contain arguments which will turn it into an argument-as-subcommand type.
 *
 * @param <S> The sender type to be used.
 */
public class InternalBranchCommand<D, S, ST> extends InternalParentCommand<D, S, ST> {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;

    private final Object invocationInstance;
    private final Constructor<?> constructor;
    private final boolean isStatic;
    private final StringInternalArgument<S, ST> argument;
    private final boolean hasArgument;

    public InternalBranchCommand(
            final @NotNull Object invocationInstance,
            final @NotNull Constructor<?> constructor,
            final boolean isStatic,
            final @Nullable StringInternalArgument<S, ST> argument,
            final @NotNull BranchCommandProcessor<D, S, ST> processor,
            final @NotNull InternalCommand<D, S, ST> parentCommand
    ) {
        super(processor);

        this.invocationInstance = invocationInstance;
        this.constructor = constructor;
        this.isStatic = isStatic;
        this.argument = argument;
        this.hasArgument = argument != null;

        this.name = processor.getName();
        this.description = getMeta().getOrDefault(MetaKey.DESCRIPTION, "");
        this.aliases = processor.getAliases();
        this.syntax = createSyntax(parentCommand, processor);
    }

    public void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments
    ) throws Throwable {
        // Test all requirements before continuing.
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        // First, we handle the argument if there is any.
        final Object instance;
        if (hasArgument) {
            final String argumentName = arguments.peek() == null ? "" : arguments.pop();

            final @NotNull InternalArgumentResult result =
                    argument.resolve(sender, new ArgumentInput(argumentName));

            if (result instanceof InternalArgumentResult.Invalid) {
                getMessageRegistry().sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((InternalArgumentResult.Invalid) result).getFail().apply(getMeta(), syntax)
                );
                return;
            }

            if (!(result instanceof InternalArgumentResult.Valid)) {
                throw new CommandExecutionException("An error occurred resolving arguments", "", name);
            }

            instance = createInstanceWithArgument(
                    instanceSupplier, ((InternalArgumentResult.Valid) result).getValue()
            );
        } else {
            instance = createInstance(instanceSupplier);
        }

        // Execute the command with the given instance.
        findAndExecute(sender, () -> instance, arguments);
    }

    @Override
    public @NotNull List<ST> suggestions(@NotNull final S sender, final @NotNull Deque<String> arguments) {
        // If we're dealing with only 1 argument, it means it's the argument suggestion
        if (arguments.size() == 1 && hasArgument) {
            return argument.suggestions(sender, arguments.peekLast(), new ArrayList<>(arguments), Collections.emptyMap());
        }

        // If we do have arguments, we need to pop them out before continuing
        if (hasArgument) arguments.pop();
        return super.suggestions(sender, arguments);
    }

    /**
     * Creates a new instance to be passed down to the child commands.
     *
     * @param instanceSupplier The instance supplier from parents.
     * @return An instance of this command for execution.
     */
    public @NotNull Object createInstance(final @Nullable Supplier<Object> instanceSupplier) {
        try {
            // Non-static classes required parent instance
            if (!isStatic) {
                return constructor.newInstance(instanceSupplier == null ? invocationInstance : instanceSupplier.get());
            }

            return constructor.newInstance();
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            throw new CommandExecutionException("An error occurred while creating the command instance")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
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

    private @NotNull String createSyntax(final @NotNull InternalCommand<D, S, ST> parentCommand,
            final @NotNull CommandProcessor<D, S, ST> processor) {
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
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return aliases;
    }

    @Override
    public @NotNull String getSyntax() {
        return syntax;
    }

    @Override
    public boolean hasArguments() {
        return argument != null;
    }

    @Override
    public boolean isHidden() {
        return argument != null;
    }
}
