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

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.SimpleSubCommand;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PrefixedCommand<S> implements Command {

    private final Map<String, SimpleSubCommand<S>> subCommands = new HashMap<>();

    private final String name;
    private final List<String> alias;

    private final ArgumentRegistry<S> argumentRegistry;
    private final MessageRegistry<S> messageRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final SenderMapper<S, PrefixedSender> senderMapper;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    public PrefixedCommand(
            @NotNull final PrefixedCommandProcessor<S> processor,
            @NotNull final ExecutionProvider syncExecutionProvider,
            @NotNull final ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();
        this.alias = processor.getAlias();
        this.argumentRegistry = processor.getArgumentRegistry();
        this.messageRegistry = processor.getMessageRegistry();
        this.requirementRegistry = processor.getRequirementRegistry();
        this.senderMapper = processor.getSenderMapper();

        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public List<String> getAlias() {
        return alias;
    }

    @Override
    public void addSubCommands(@NotNull final BaseCommand baseCommand) {
        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            final PrefixedSubCommandProcessor<S> processor = new PrefixedSubCommandProcessor<>(
                    baseCommand,
                    method,
                    argumentRegistry,
                    requirementRegistry,
                    messageRegistry,
                    senderMapper
            );

            final String subCommandName = processor.getName();
            if (subCommandName == null) continue;

            final ExecutionProvider executionProvider = processor.isAsync() ? asyncExecutionProvider : syncExecutionProvider;

            final SimpleSubCommand<S> subCommand = subCommands.computeIfAbsent(subCommandName, s -> new SimpleSubCommand<>(processor, name, executionProvider));
            for (final String alias : processor.getAlias()) {
                subCommands.putIfAbsent(alias, subCommand);
            }
        }
    }

    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
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

        subCommand.execute(sender, args);
    }

    @Nullable
    private SubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    @Nullable
    private SubCommand<S> getSubCommand(@NotNull final String key) {
        return subCommands.get(key);
    }

    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key);
    }

}
