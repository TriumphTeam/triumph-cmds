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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

final class BukkitCommand<S> extends Command {

    private final RootCommand<CommandSender, S> rootCommand;
    private final SenderExtension<CommandSender, S> senderExtension;

    BukkitCommand(final @NotNull RootCommandProcessor<CommandSender, S> processor) {
        super(processor.getName(), processor.getDescription(), "", processor.getAliases());

        this.rootCommand = new RootCommand<>(processor);
        this.senderExtension = processor.getCommandOptions().getCommandExtensions().getSenderExtension();
    }

    @Override
    public boolean execute(
            final @NotNull CommandSender sender,
            final @NotNull String commandLabel,
            final @NotNull String[] args
    ) {
        rootCommand.execute(
                senderExtension.map(sender),
                null,
                new ArrayDeque<>(Arrays.asList(args))
        );
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(
            final @NotNull CommandSender sender,
            final @NotNull String alias,
            final @NotNull String[] args
    ) {
        return rootCommand.suggestions(senderExtension.map(sender), new ArrayDeque<>(Arrays.asList(args)));
    }

    public @NotNull RootCommand<CommandSender, S> getRootCommand() {
        return rootCommand;
    }
}
