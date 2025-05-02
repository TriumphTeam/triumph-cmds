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
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.message.context.SyntaxMessageContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import dev.triumphteam.cmd.core.processor.LeafCommandProcessor;
import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@SuppressWarnings("unchecked")
public class InternalLeafCommand<D, S, ST> implements InternalCommand<D, S, ST> {

    private final Class<? extends S> senderType;

    private final List<InternalArgument<S, ST>> argumentList;
    private final Map<String, InternalArgument<S, ST>> argumentMap;

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;
    private final boolean containsLimitless;

    private final CommandMeta meta;
    private final Settings<D, S> settings;

    private final Object invocationInstance;
    private final Method method;
    private final CommandExecutor<S> commandExecutor;

    private final SenderExtension<D, S> senderExtension;
    private final MessageRegistry<S> messageRegistry;

    public InternalLeafCommand(
            final @NotNull Object invocationInstance,
            final @NotNull Method method,
            final @NotNull LeafCommandProcessor<D, S, ST> processor,
            final @NotNull InternalCommand<D, S, ST> parentCommand
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

        final CommandOptions<D, S, ?, ST> commandOptions = processor.getCommandOptions();

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = commandOptions.getCommandExtensions().getSenderExtension();
        this.commandExecutor = commandOptions.getCommandExtensions().getCommandExecutor();

        this.syntax = createSyntax(parentCommand, processor);

        this.settings = settingsBuilder.build();
    }

    public void execute(
            final @NotNull S sender,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull Map<String, ArgumentInput> arguments
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

        if ((!containsLimitless) && arguments.size() > argumentList.size()) {
            messageRegistry.sendMessage(MessageKey.TOO_MANY_ARGUMENTS, sender, new SyntaxMessageContext(meta, syntax));
            return;
        }

        for (final InternalArgument<S, ST> internalArgument : argumentList) {
            final ArgumentInput argumentInput = arguments.get(internalArgument.getName());

            final InternalArgumentResult result;
            if (internalArgument instanceof LimitlessInternalArgument) {
                final LimitlessInternalArgument<S, ST> limitlessArgument = (LimitlessInternalArgument<S, ST>) internalArgument;

                // From this point on [commandArgs] is treated as a simple Collection instead of Deque
                result = limitlessArgument.resolve(sender, argumentInput == null ? new ArgumentInput("") : argumentInput);
            } else if (internalArgument instanceof StringInternalArgument) {
                final StringInternalArgument<S, ST> stringArgument = (StringInternalArgument<S, ST>) internalArgument;

                ArgumentInput usableInput = argumentInput;
                if (argumentInput == null || argumentInput.getInput().isEmpty()) {
                    // TODO(important): ADD OPTIONAL VALUE PROVIDER
                    if (internalArgument.isOptional()) {
                        usableInput = new ArgumentInput("TODO");
                    } else {
                        messageRegistry.sendMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, sender, new SyntaxMessageContext(meta, syntax));
                        return;
                    }
                }
                result = stringArgument.resolve(sender, usableInput);
            } else {
                // Should never happen, this should be a sealed type ... but hey, it's Java 8
                throw new CommandExecutionException("Found unsupported argument", "", name);
            }

            // In case of failure, we send the Sender a message.
            if (result instanceof InternalArgumentResult.Invalid) {
                messageRegistry.sendMessage(
                        MessageKey.INVALID_ARGUMENT,
                        sender,
                        ((InternalArgumentResult.Invalid) result).getFail().apply(meta, syntax)
                );
                return;
            }

            // In case of success, we add the results.
            if (result instanceof InternalArgumentResult.Valid) {
                invokeArguments.add(((InternalArgumentResult.Valid) result).getValue());
            }
        }

        commandExecutor.execute(
                meta,
                messageRegistry,
                sender,
                instanceSupplier == null ? invocationInstance : instanceSupplier.get(),
                method,
                invokeArguments
        );
    }

    public @NotNull Map<String, ArgumentInput> mapArguments(final @NotNull Deque<String> arguments) {
        final Map<String, ArgumentInput> mappedArguments = new HashMap<>();

        int index = 0;
        while (!arguments.isEmpty()) {
            final String arg = arguments.peek();
            final InternalArgument<S, ST> internalArgument = getArgument(index);

            if (internalArgument == null || arg.isEmpty()) {
                mappedArguments.put(String.valueOf(index), new ArgumentInput(arg));
                arguments.pop(); // Pop before continuing.
                index++; // increment before continuing.
                continue;
            }

            index++; // Increment early so it doesn't need to repeat later.

            if (internalArgument instanceof LimitlessInternalArgument) {
                // Join all leftover arguments.
                mappedArguments.put(internalArgument.getName(), new ArgumentInput(String.join(" ", arguments)));
                break;
            }

            if (!(internalArgument instanceof StringInternalArgument)) {
                // Should never happen, this should be a sealed type ... but hey, it's Java 8.
                throw new CommandExecutionException("Found unsupported argument", "", name);
            }

            arguments.pop(); // Pop here so we don't remove the first argument of a "limitless".
            mappedArguments.put(internalArgument.getName(), new ArgumentInput(arg));
        }

        return mappedArguments;
    }

    public @NotNull List<ST> suggestions(
            final @NotNull S sender,
            final @NotNull List<String> arguments
    ) {
        if (arguments.isEmpty()) return emptyList();

        final int index = arguments.size() - 1;
        final InternalArgument<S, ST> argument = getArgumentFromIndex(index);
        if (argument == null) return emptyList();

        if (arguments.isEmpty()) {
            return argument.suggestions(sender, "", arguments);
        }

        final String current = arguments.get(index);
        return argument.suggestions(sender, current, arguments);
    }

    public @Nullable InternalArgument<S, ST> getArgumentFromIndex(final int index) {
        if (!hasArguments()) return null;
        final int size = argumentList.size();
        if (index >= size) {
            final InternalArgument<S, ST> last = argumentList.get(size - 1);
            if (last instanceof LimitlessInternalArgument) return last;
            return null;
        }

        return argumentList.get(index);
    }

    private @NotNull String createSyntax(
            final @NotNull InternalCommand<D, S, ST> parentCommand,
            final @NotNull CommandProcessor<D, S, ST> processor
    ) {
        final Syntax syntaxAnnotation = processor.getSyntaxAnnotation();
        if (syntaxAnnotation != null) return syntaxAnnotation.value();

        final StringBuilder builder = new StringBuilder(parentCommand.getSyntax());

        if (!InternalCommand.DEFAULT_CMD_NAME.equals(name)) {
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

    public @NotNull List<InternalArgument<S, ST>> getArgumentList() {
        return argumentList;
    }

    public @Nullable InternalArgument<S, ST> getArgument(final int index) {
        if (index >= argumentList.size()) return null;
        return argumentList.get(index);
    }

    public @Nullable InternalArgument<S, ST> getArgument(final @NotNull String name) {
        return argumentMap.get(name);
    }

    @Override
    public boolean isDefault() {
        return name.equals(InternalCommand.DEFAULT_CMD_NAME);
    }

    @Override
    public boolean isHidden() {
        return isDefault();
    }

    @Override
    public boolean hasArguments() {
        return !argumentList.isEmpty();
    }
}
