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
package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.subcommand.SubCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimpleCommand<S> implements Command<S, SimpleSubCommand<S>> {

    private final String name;

    private final RegistryContainer<S> registries;
    private final MessageRegistry<S> messageRegistry;

    private final SenderMapper<S, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    private final Map<String, SimpleSubCommand<S>> subCommands = new HashMap<>();
    private final Map<String, SimpleSubCommand<S>> subCommandAliases = new HashMap<>();

    @SuppressWarnings("unchecked")
    public SimpleCommand(
            final @NotNull SimpleCommandProcessor<S> processor,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();

        this.senderMapper = processor.getSenderMapper();
        this.senderValidator = processor.getSenderValidator();
        this.registries = processor.getRegistryContainer();
        this.messageRegistry = registries.getMessageRegistry();
        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    // TODO: Comments
    public void execute(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> args
    ) {
        SimpleSubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.size() > 0) subCommandName = args.get(0).toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        final S mappedSender = senderMapper.map(sender);
        if (mappedSender == null) {
            throw new CommandExecutionException("Invalid sender. Sender mapper returned null");
        }

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(name, subCommandName));
            return;
        }

        final List<String> commandArgs = !subCommand.isDefault() ? args.subList(1, args.size()) : args;
        subCommand.execute(mappedSender, commandArgs);
    }

    /**
     * Gets a default command if present.
     *
     * @return A default SubCommand.
     */
    private @Nullable SimpleSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Used in order to search for the given {@link SubCommand<S>} in the {@link #subCommandAliases}
     *
     * @param key the String to look for the {@link SubCommand<S>}
     * @return the {@link SubCommand<S>} for the particular key or NULL
     */
    private @Nullable SimpleSubCommand<S> getSubCommand(final @NotNull String key) {
        final SimpleSubCommand<S> subCommand = subCommands.get(key);
        if (subCommand != null) return subCommand;
        return subCommandAliases.get(key);
    }

    /**
     * Checks if a SubCommand with the specified key exists.
     *
     * @param key the Key to check for
     * @return whether a SubCommand with that key exists
     */
    private boolean subCommandExists(final @NotNull String key) {
        return subCommands.containsKey(key) || subCommandAliases.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull SimpleSubCommand<S> subCommand) {
        this.subCommands.put(name, subCommand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubCommandAlias(final @NotNull String alias, final @NotNull SimpleSubCommand<S> subCommand) {
        this.subCommandAliases.put(alias, subCommand);
    }
}
