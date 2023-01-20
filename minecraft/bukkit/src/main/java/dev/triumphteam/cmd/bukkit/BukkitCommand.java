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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.command.ExecutableCommand;
import dev.triumphteam.cmd.core.command.ParentCommand;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

final class BukkitCommand<S> extends Command implements ParentCommand<S> {

    BukkitCommand(
            final @NotNull RootCommandProcessor<S, S> processor,
            final @NotNull MessageRegistry<S> messageRegistry
    ) {
        super(processor.getName());
    }

    @Override
    public void addSubCommand(final @NotNull ExecutableCommand<S> subCommand, final boolean isAlias) {

    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
        return false;
    }

    @Override
    public @NotNull String getSyntax() {
        return null;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommands() {
        return null;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommandAliases() {
        return null;
    }

    @Override
    public @Nullable ExecutableCommand<S> getParentCommandWithArgument() {
        return null;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return null;
    }
}
