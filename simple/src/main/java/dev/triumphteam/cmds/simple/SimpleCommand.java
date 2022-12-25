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
package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.command.Command;
import dev.triumphteam.cmd.core.command.ParentCommand;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimpleCommand<S> implements ParentCommand<S> {

    private final String name;

    private final MessageRegistry<S> messageRegistry;

    private final Map<String, Command<S>> subCommands = new HashMap<>();
    private final Map<String, Command<S>> subCommandAliases = new HashMap<>();

    @SuppressWarnings("unchecked")
    public SimpleCommand(
            final @NotNull SimpleCommandProcessor<S> processor,
            final @NotNull MessageRegistry<S> messageRegistry
    ) {
        this.name = processor.getName();

        this.messageRegistry = messageRegistry;
    }

    // TODO: Comments
    public void execute(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> arguments
    ) {
        final int argumentSize = arguments.size();

        final Command<S> subCommand = getSubCommand(arguments);

        // TODO
        /*if (subCommand == null || (argumentSize > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            final String name = argumentSize == 0 ? dev.triumphteam.cmd.core.annotation.Command.DEFAULT_CMD_NAME : arguments.get(0);
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new DefaultMessageContext(this.name, name));
            return;
        }

        subCommand.execute(sender, !subCommand.isDefault() ? arguments.subList(1, argumentSize) : arguments);*/
    }

    @Override
    public void addSubCommand(
            final @NotNull String name,
            final @NotNull Command<S> subCommand,
            final boolean isAlias
    ) {
        subCommands.put(name, subCommand);
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommands() {
        return subCommands;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommandAliases() {
        return subCommandAliases;
    }
}
