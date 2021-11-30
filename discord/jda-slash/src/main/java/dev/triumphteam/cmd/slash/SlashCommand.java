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
package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main implementation of the command for prefixed JDA.
 *
 * @param <S> The sender type.
 */
final class SlashCommand<S> implements Command {

    private final Map<String, SlashSubCommand<S>> subCommands = new HashMap<>();

    private final String name;
    private final String description;

    private final List<Long> enabledRoles;
    private final List<Long> disabledRoles;

    private final ArgumentRegistry<S> argumentRegistry;
    private final MessageRegistry<S> messageRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final SenderMapper<S, SlashSender> senderMapper;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    private boolean isDefault = false;

    public SlashCommand(
            @NotNull final SlashCommandProcessor<S> processor,
            @NotNull final List<Long> enabledRoles,
            @NotNull final List<Long> disabledRoles,
            @NotNull final ExecutionProvider syncExecutionProvider,
            @NotNull final ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();
        this.description = processor.getDescription();
        this.argumentRegistry = processor.getArgumentRegistry();
        this.messageRegistry = processor.getMessageRegistry();
        this.requirementRegistry = processor.getRequirementRegistry();
        this.senderMapper = processor.getSenderMapper();

        this.enabledRoles = enabledRoles;
        this.disabledRoles = disabledRoles;

        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    public List<Long> getEnabledRoles() {
        return enabledRoles;
    }

    public List<Long> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubCommands(@NotNull final BaseCommand baseCommand) {
        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            final SlashSubCommandProcessor<S> processor = new SlashSubCommandProcessor<>(
                    baseCommand,
                    method,
                    argumentRegistry,
                    requirementRegistry,
                    messageRegistry,
                    senderMapper
            );

            final String subCommandName = processor.getName();
            if (subCommandName == null) continue;

            // TODO: 11/27/2021 Remove repeating code for throwing the exception.
            if (isDefault) {
                throw new CommandRegistrationException("Default commands cannot be registered with subcommands", baseCommand.getClass());
            }

            if (processor.isDefault()) {
                if (subCommands.size() > 0) {
                    throw new CommandRegistrationException("Default commands cannot be registered with subcommands", baseCommand.getClass());
                }

                isDefault = true;
            }

            final ExecutionProvider executionProvider = processor.isAsync() ? asyncExecutionProvider : syncExecutionProvider;

            subCommands.putIfAbsent(subCommandName, new SlashSubCommand<>(processor, name, executionProvider));
        }
    }

    /**
     * Executes the current command for the given sender.
     *
     * @param sender The sender.
     * @param args   The command arguments.
     */
    public void execute(
            @NotNull final S sender,
            @NotNull final String subCommandName,
            @NotNull final List<String> args
    ) {
        final SubCommand<S> subCommand = getSubCommand(subCommandName);
        if (subCommand == null) return;
        subCommand.execute(sender, args);
    }

    @NotNull
    public CommandData asCommandData() {
        final CommandData commandData = new CommandData(name, description);
        commandData.setDefaultEnabled(enabledRoles.isEmpty());

        if (isDefault) {
            final SlashSubCommand<S> subCommand = getDefaultSubCommand();
            // Should never be null.
            if (subCommand == null) throw new CommandRegistrationException("Could not find default subcommand");
            commandData.addOptions(subCommand.getJdaOptions());
            return commandData;
        }

        final List<SubcommandData> subData = subCommands
                .entrySet()
                .stream()
                .map(entry -> new SubcommandData(entry.getKey(), entry.getValue().getDescription()).addOptions(entry.getValue().getJdaOptions()))
                .collect(Collectors.toList());

        commandData.addSubcommands(subData);
        return commandData;
    }

    /**
     * Gets the default sub command or null if there is none.
     *
     * @return The default sub command.
     */
    @Nullable
    private SlashSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Gets the valid sub command for the given name or null if there is none.
     *
     * @param key The sub command name.
     * @return A sub command or null.
     */
    @Nullable
    private SlashSubCommand<S> getSubCommand(@NotNull final String key) {
        return subCommands.get(key);
    }

    /**
     * Checks if the given sub command exists.
     *
     * @param key The sub command name.
     * @return True if the sub command exists.
     */
    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key);
    }

}
