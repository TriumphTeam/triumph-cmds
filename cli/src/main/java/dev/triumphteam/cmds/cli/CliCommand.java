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
package dev.triumphteam.cmds.cli;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmds.cli.sender.CliSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CliCommand<S> implements Command<S, CliSubCommand<S>> {

    private final String name;

    private final RegistryContainer<S> registries;
    private final MessageRegistry<S> messageRegistry;

    private final SenderMapper<CliSender, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    private final Map<String, CliSubCommand<S>> subCommands = new HashMap<>();
    private final Map<String, CliSubCommand<S>> subCommandAliases = new HashMap<>();

    @SuppressWarnings("unchecked")
    public CliCommand(
            @NotNull final CliCommandProcessor<S> processor,
            @NotNull final ExecutionProvider syncExecutionProvider,
            @NotNull final ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();

        this.senderMapper = processor.getSenderMapper();
        this.senderValidator = processor.getSenderValidator();
        this.registries = processor.getRegistryContainer();
        this.messageRegistry = registries.getMessageRegistry();
        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    /**
     * adds SubCommands from the Command.
     *
     * @param baseCommand The {@link BaseCommand} to get the sub commands from.
     */
    public void addSubCommands(@NotNull final BaseCommand baseCommand) {
        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) continue;

            final CliSubCommandProcessor<S> processor = new CliSubCommandProcessor<>(
                    baseCommand,
                    name,
                    method,
                    registries,
                    senderValidator
            );

            final String subCommandName = processor.getName();
            if (subCommandName == null) continue;

            final ExecutionProvider executionProvider = processor.isAsync() ? asyncExecutionProvider : syncExecutionProvider;
            final CliSubCommand<S> subCommand = subCommands.computeIfAbsent(subCommandName, it -> new CliSubCommand<>(processor, name, executionProvider));
            processor.getAlias().forEach(alias -> subCommandAliases.putIfAbsent(alias, subCommand));
        }
    }

    // TODO: Comments
    public void execute(
            @NotNull final CliSender sender,
            @NotNull final String[] args
    ) {
        CliSubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        final S mappedSender = senderMapper.map(sender);

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(name, subCommandName));
            return;
        }

        final List<String> commandArgs = Arrays.asList(!subCommand.isDefault() ? Arrays.copyOfRange(args, 1, args.length) : args);
        subCommand.execute(mappedSender, commandArgs);
    }

    /**
     * Gets a default command if present.
     *
     * @return A default SubCommand.
     */
    @Nullable
    private CliSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Used in order to search for the given {@link SubCommand<S>} in the {@link #subCommandAliases}
     *
     * @param key the String to look for the {@link SubCommand<S>}
     * @return the {@link SubCommand<S>} for the particular key or NULL
     */
    @Nullable
    private CliSubCommand<S> getSubCommand(@NotNull final String key) {
        final CliSubCommand<S> subCommand = subCommands.get(key);
        if (subCommand != null) return subCommand;
        return subCommandAliases.get(key);
    }

    /**
     * Checks if a SubCommand with the specified key exists.
     *
     * @param key the Key to check for
     * @return whether a SubCommand with that key exists
     */
    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key) || subCommandAliases.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubCommands(@NotNull Map<String, CliSubCommand<S>> subCommands, @NotNull Map<String, CliSubCommand<S>> subCommandAliases) {
        this.subCommands.putAll(subCommands);
        this.subCommandAliases.putAll(subCommandAliases);
    }
}
