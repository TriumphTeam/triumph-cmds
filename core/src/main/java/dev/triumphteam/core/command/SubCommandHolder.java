/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.core.command;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.command.message.MessageKey;
import dev.triumphteam.core.command.message.MessageRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SubCommandHolder<S> {

    private final MessageRegistry<S> messageRegistry;
    private final List<SubCommand<S>> subCommands = new ArrayList<>();
    private int argumentSize;
    private final boolean isDefault;

    public SubCommandHolder(@NotNull final MessageRegistry<S> messageRegistry, @NotNull final SubCommand<S> subCommand) {
        this.messageRegistry = messageRegistry;

        subCommands.add(subCommand);
        argumentSize = subCommand.getArguments().size();
        isDefault = subCommand.getName().equals(Default.DEFAULT_CMD_NAME);
    }

    public void add(@NotNull final SubCommand<S> subCommand) {
        subCommands.add(subCommand);
        final int newArgumentSize = subCommand.getArguments().size();
        // TODO: Still need to think about this
        if (newArgumentSize > argumentSize) argumentSize = newArgumentSize;
        if (subCommands.size() <= 1) return;
        // Sorts subcommands by priority on registration so no need to check on execution
        subCommands.sort(Comparator.comparingInt(SubCommand::getPriority));
    }

    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        CommandExecutionResult lastResult = CommandExecutionResult.WRONG_USAGE;
        for (final SubCommand<S> subCommand : subCommands) {
            final CommandExecutionResult result = subCommand.execute(sender, args);
            if (result == CommandExecutionResult.SUCCESS) {
                System.out.println("Executed correctly using subcommand `" + subCommand.getMethod().getName() + "`!");
                return;
            }

            lastResult = result;
        }

        messageRegistry.sendMessage(MessageKey.of(lastResult.key()), sender);
    }

    public boolean isSmallDefault() {
        return isDefault && argumentSize == 0;
    }

    @Override
    public String toString() {
        return "SubCommandHolder{" +
                "subCommands=" + subCommands +
                '}';
    }
}
