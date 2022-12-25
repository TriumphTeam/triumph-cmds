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
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main executor for the commands.
 *
 * @param <S> The sender type.
 */
final class PrefixedCommandExecutor<S> {

    private final Map<String, PrefixedCommand<S>> commands = new HashMap<>();

    private final MessageRegistry<S> messageRegistry;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    public PrefixedCommandExecutor(
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.messageRegistry = messageRegistry;
        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    /**
     * Registers a command to the current executor.
     *
     * @param processor The processor with all the command data.
     */
    public void register(final @NotNull PrefixedCommandProcessor<S> processor) {
        final String name = processor.getName();

        final PrefixedCommand<S> command = commands.computeIfAbsent(name, p -> new PrefixedCommand<>(processor, syncExecutionProvider, asyncExecutionProvider));

        for (final String alias : processor.getAlias()) {
            commands.putIfAbsent(alias, command);
        }

        processor.addSubCommands(command);
    }

    /**
     * Executes the given command for the given sender.
     *
     * @param commandName The command name.
     * @param sender      The command sender.
     * @param args        The command arguments.
     */
    public void execute(
            final @NotNull String commandName,
            final @NotNull S sender,
            final @NotNull List<@NotNull String> args
    ) {

        final PrefixedCommand<S> command = commands.get(commandName);
        if (command == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new DefaultMessageContext(commandName, ""));
            return;
        }

        command.execute(sender, args);
    }

}
