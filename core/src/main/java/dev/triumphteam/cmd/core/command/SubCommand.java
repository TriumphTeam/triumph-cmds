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
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.ValidationResult;
import dev.triumphteam.cmd.core.extention.command.Settings;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.message.context.SyntaxMessageContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

@SuppressWarnings("unchecked")
public class SubCommand<D, S> implements Command<D, S> {

    private final Class<? extends S> senderType;

    private final List<InternalArgument<S, ?>> argumentList;
    private final Map<String, InternalArgument<S, ?>> argumentMap;

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;
    private final boolean containsLimitless;

    private final CommandMeta meta;
    private final Settings<D, S> settings;

    private final Object invocationInstance;
    private final Method method;
    private final CommandExecutor commandExecutor;

    private final SenderExtension<D, S> senderExtension;
    private final MessageRegistry<S> messageRegistry;

    public SubCommand(
            final @NotNull Object invocationInstance,
            final @NotNull Method method,
            final @NotNull SubCommandProcessor<D, S> processor,
            final @NotNull Command<D, S> parentCommand
    ) {
        this.invocationInstance = invocationInstance;
        this.method = method;
        this.name = processor.getName();
        this.description = processor.getDescription();
        this.aliases = processor.getAliases();

        final Settings.Builder<D, S> settingsBuilder = new Settings.Builder<>();
        processor.captureRequirements(settingsBuilder);
        this.meta = processor.createMeta(settingsBuilder);

        this.senderType = processor.senderType();
        this.argumentList = processor.arguments(meta);
        this.argumentMap = this.argumentList.stream()
                .map(argument -> new Pair<>(argument.getName(), argument))
                .collect(Collectors.toMap(Pair::first, Pair::second));

        this.containsLimitless = argumentList.stream().anyMatch(LimitlessInternalArgument.class::isInstance);

        final CommandOptions<D, S> commandOptions = processor.getCommandOptions();

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = commandOptions.getSenderExtension();
        this.commandExecutor = commandOptions.getCommandExtensions().getCommandExecutor();

        this.syntax = createSyntax(parentCommand, processor);

        this.settings = settingsBuilder.build();
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> arguments
    ) throws Throwable {
        final ValidationResult<MessageKey<MessageContext>> validationResult = senderExtension.validate(meta, senderType, sender);

        // If the result is invalid for a reason given by the validator, we stop the execution and use its key to send
        // a message to the sender
        if (validationResult instanceof ValidationResult.Invalid) {
            messageRegistry.sendMessage(
                    ((ValidationResult.Invalid<MessageKey<MessageContext>>) validationResult).getMessage(),
                    sender,
                    new SyntaxMessageContext(meta, syntax)
            );
            return;
        }

        // Testing if all requirements pass before we continue
        if (!settings.testRequirements(messageRegistry, sender, meta, senderExtension)) return;

        // Creates the invoking arguments list
        final List<Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        if (!validateAndCollectArguments(sender, invokeArguments, arguments)) return;

        if ((!containsLimitless) && arguments.size() >= invokeArguments.size()) {
            messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new SyntaxMessageContext(meta, syntax));
            return;
        }

        commandExecutor.execute(
                meta,
                instanceSupplier == null ? invocationInstance : instanceSupplier.get(),
                method,
                invokeArguments
        );
    }

    @Override
    public void executeNonLinear(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Deque<String> commands,
            final @NotNull Map<String, Function<Class<?>, Pair<String, Object>>> arguments
    ) throws Throwable {
        // TODO DRY
        final ValidationResult<MessageKey<MessageContext>> validationResult = senderExtension.validate(meta, senderType, sender);

        // If the result is invalid for a reason given by the validator, we stop the execution and use its key to send
        // a message to the sender
        if (validationResult instanceof ValidationResult.Invalid) {
            messageRegistry.sendMessage(
                    ((ValidationResult.Invalid<MessageKey<MessageContext>>) validationResult).getMessage(),
                    sender,
                    new SyntaxMessageContext(meta, syntax)
            );
            return;
        }

        // Testing if all requirements pass before we continue
        if (!settings.testRequirements(messageRegistry, sender, meta, senderExtension)) return;

        // Creates the invoking arguments list
        final List<Object> invokeArguments = new ArrayList<>();
        invokeArguments.add(sender);

        argumentList.forEach(it -> {
            final Function<Class<?>, Pair<String, Object>> function = arguments.get(it.getName());
            // Should only really happen on optional arguments
            if (function == null) {
                invokeArguments.add(null);
                return;
            }

            final Pair<String, Object> pair = function.apply(it.getType());

            final Deque<String> raw;
            if (it instanceof LimitlessInternalArgument) {
                raw = new ArrayDeque<>(Arrays.asList(pair.first().split("")));
            } else {
                raw = new ArrayDeque<>(Collections.singleton(pair.first()));
            }

            validateAndCollectArgument(sender, invokeArguments, raw, it, pair.second());
        });

        commandExecutor.execute(
                meta,
                instanceSupplier == null ? invocationInstance : instanceSupplier.get(),
                method,
                invokeArguments
        );
    }

    @Override
    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    ) {
        if (arguments.isEmpty()) return emptyList();

        final int index = arguments.size() - 1;
        final InternalArgument<S, ?> argument = getArgumentFromIndex(index);
        if (argument == null) return emptyList();

        return argument.suggestions(sender, arguments);
    }

    public @Nullable InternalArgument<S, ?> getArgumentFromIndex(final int index) {
        if (!hasArguments()) return null;
        final int size = argumentList.size();
        if (index >= size) {
            final InternalArgument<S, ?> last = argumentList.get(size - 1);
            if (last instanceof LimitlessInternalArgument) return last;
            return null;
        }

        return argumentList.get(index);
    }

    public @NotNull List<String> suggest(
            final @NotNull S sender,
            final @NotNull String name,
            final @NotNull String value
    ) {
        final InternalArgument<S, ?> argument = getArgumentFromName(name);
        if (argument == null) return emptyList();
        return argument.suggestions(sender, new ArrayDeque<>(singleton(value)));
    }

    private @Nullable InternalArgument<S, ?> getArgumentFromName(final @NotNull String name) {
        return argumentMap.get(name);
    }

    /**
     * Used for checking if the arguments are valid and adding them to the `invokeArguments`.
     *
     * @param sender          The sender of the command.
     * @param invokeArguments A list with the arguments that'll be used on the `invoke` of the command method.
     * @param commandArgs     The command arguments type.
     * @return False if any internalArgument fails to pass.
     */
    private boolean validateAndCollectArguments(
            final @NotNull S sender,
            final @NotNull List<Object> invokeArguments,
            final @NotNull Deque<String> commandArgs
    ) {
        for (final InternalArgument<S, ?> internalArgument : argumentList) {
            if (!validateAndCollectArgument(sender, invokeArguments, commandArgs, internalArgument, null)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateAndCollectArgument(
            final @NotNull S sender,
            final @NotNull List<Object> invokeArguments,
            final @NotNull Deque<String> commandArgs,
            final @NotNull InternalArgument<S, ?> internalArgument,
            final @Nullable Object provided
    ) {
        final Result<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>> result;
        if (internalArgument instanceof LimitlessInternalArgument) {
            final LimitlessInternalArgument<S> limitlessArgument = (LimitlessInternalArgument<S>) internalArgument;

            // From this point on [commandArgs] is treated as a simple Collection instead of Deque
            result = limitlessArgument.resolve(sender, commandArgs, provided);
        } else if (internalArgument instanceof StringInternalArgument) {
            final StringInternalArgument<S> stringArgument = (StringInternalArgument<S>) internalArgument;
            final String arg = commandArgs.peek();

            if (arg == null || arg.isEmpty()) {
                if (internalArgument.isOptional()) {
                    invokeArguments.add(null);
                    return true;
                }

                messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new SyntaxMessageContext(meta, syntax));
                return false;
            }

            // Pop the command out
            commandArgs.pop();
            result = stringArgument.resolve(sender, arg, provided);
        } else {
            // Should never happen, this should be a sealed type ... but hey, it's Java 8
            throw new CommandExecutionException("Found unsupported argument", "", name);
        }

        // In case of failure we send the Sender a message
        if (result instanceof Result.Failure) {
            messageRegistry.sendMessage(
                    MessageKey.INVALID_ARGUMENT,
                    sender,
                    ((Result.Failure<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result)
                            .getFail()
                            .apply(meta, syntax)
            );
            return false;
        }

        // In case of success we add the results
        if (result instanceof Result.Success) {
            invokeArguments.add(((Result.Success<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) result).getValue());
        }

        return true;
    }

    private @NotNull String createSyntax(
            final @NotNull Command<D, S> parentCommand,
            final @NotNull CommandProcessor<D, S> processor
    ) {
        final Syntax syntaxAnnotation = processor.getSyntaxAnnotation();
        if (syntaxAnnotation != null) return syntaxAnnotation.value();

        final StringBuilder builder = new StringBuilder(parentCommand.getSyntax());

        if (!dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME.equals(name)) {
            builder.append(" ").append(name);
        }

        argumentList.forEach(argument -> builder.append(" ").append("<").append(argument.getName()).append(">"));

        return builder.toString();
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    @Override
    public @NotNull Settings<D, S> getCommandSettings() {
        return settings;
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

    public @NotNull List<InternalArgument<S, ?>> getArgumentList() {
        return argumentList;
    }

    public @NotNull Map<String, InternalArgument<S, ?>> getArgumentMap() {
        return argumentMap;
    }

    @Override
    public boolean isDefault() {
        return name.equals(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME);
    }

    @Override
    public boolean hasArguments() {
        return !argumentList.isEmpty();
    }
}
