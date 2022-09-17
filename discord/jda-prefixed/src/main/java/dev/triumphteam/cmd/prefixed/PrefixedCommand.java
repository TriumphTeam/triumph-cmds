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
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.subcommand.SubCommand;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main implementation of the command for prefixed JDA.
 *
 * @param <S> The sender type.
 */
final class PrefixedCommand<S> implements Command<S, PrefixedSubCommand<S>> {

    private final Map<String, PrefixedSubCommand<S>> subCommands = new HashMap<>();

    private final String name;
    private final List<String> alias;

    private final RegistryContainer<S> registryContainer;

    private final SenderMapper<PrefixedSender, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    public PrefixedCommand(
            final @NotNull PrefixedCommandProcessor<S> processor,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();
        this.alias = processor.getAlias();
        this.registryContainer = processor.getRegistryContainer();
        this.senderMapper = processor.getSenderMapper();
        this.senderValidator = processor.getSenderValidator();

        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull PrefixedSubCommand<S> subCommand) {
        this.subCommands.putIfAbsent(name, subCommand);
    }

    @Override
    public void addSubCommandAlias(final @NotNull String alias, final @NotNull PrefixedSubCommand<S> subCommand) {
        this.subCommands.putIfAbsent(alias, subCommand);
    }

    /**
     * Executes the current command for the given sender.
     *
     * @param sender The sender.
     * @param args   The command arguments.
     */
    public void execute(final @NotNull S sender, final @NotNull List<@NotNull String> args) {
        SubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.size() > 0) subCommandName = args.get(0).toLowerCase();

        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        if (subCommand == null) {
            //sender.sendMessage("Command doesn't exist matey.");
            return;
        }

        // TODO: 11/28/2021 Alias check
        final List<String> arguments = !subCommand.isDefault() ? args.subList(1, args.size()) : args;
        subCommand.execute(sender, arguments);
    }

    /**
     * Gets the default sub command or null if there is none.
     *
     * @return The default sub command.
     */
    private @Nullable SubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Gets the valid sub command for the given name or null if there is none.
     *
     * @param key The sub command name.
     * @return A sub command or null.
     */
    private @Nullable SubCommand<S> getSubCommand(final @NotNull String key) {
        return subCommands.get(key);
    }

    /**
     * Checks if the given sub command exists.
     *
     * @param key The sub command name.
     * @return True if the sub command exists.
     */
    private boolean subCommandExists(final @NotNull String key) {
        return subCommands.containsKey(key);
    }

}
