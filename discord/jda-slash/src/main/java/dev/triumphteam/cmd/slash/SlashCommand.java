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

import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.slash.choices.ChoiceRegistry;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main implementation of the command for prefixed JDA.
 *
 * @param <S> The sender type.
 */
final class SlashCommand<S> implements Command<S, SlashSubCommand<S>> {

    private final Map<String, SlashSubCommand<S>> subCommands = new HashMap<>();

    private final String name;
    private final String description;

    private final List<Permission> allow;

    private final RegistryContainer<S> registryContainer;
    private final ChoiceRegistry choiceRegistry;

    private final SenderValidator<S> senderValidator;

    private final ExecutionProvider syncExecutionProvider;
    private final ExecutionProvider asyncExecutionProvider;

    private boolean isDefault = false;

    public SlashCommand(
            final @NotNull SlashCommandProcessor<S> processor,
            final @NotNull List<@NotNull Permission> allow,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        this.name = processor.getName();
        this.description = processor.getDescription();
        this.registryContainer = processor.getRegistryContainer();
        this.choiceRegistry = processor.getChoiceRegistry();
        this.senderValidator = processor.getSenderValidator();

        this.allow = allow;

        this.syncExecutionProvider = syncExecutionProvider;
        this.asyncExecutionProvider = asyncExecutionProvider;
    }

    public @NotNull List<@NotNull Permission> getAllowed() {
        return allow;
    }


    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull SlashSubCommand<S> subCommand) {
        if (name.equals(Default.DEFAULT_CMD_NAME)) {
            if (!this.subCommands.isEmpty()) {
                throw new CommandRegistrationException(String.format("Can not register default command for '%s' because it has subcommands", this.name));
            }

            this.subCommands.put(name, subCommand);
            isDefault = true;
            return;
        }

        if (isDefault) {
            throw new CommandRegistrationException(String.format("Can not register subcommand '%s' for command '%s' because it has a default command", name, this.name));
        }

        this.subCommands.putIfAbsent(name, subCommand);
    }

    @Override
    public void addSubCommandAlias(final @NotNull String alias, final @NotNull SlashSubCommand<S> subCommand) {
        // Doesn't support alias .. yet
    }

    /**
     * Executes the current command for the given sender.
     *
     * @param sender The sender.
     * @param args   The command arguments.
     */
    public void execute(
            final @NotNull S sender,
            final @NotNull String subCommandName,
            final @NotNull Map<@NotNull String, @NotNull String> args
    ) {
        final SlashSubCommand<S> subCommand = getSubCommand(subCommandName);
        if (subCommand == null) return;
        subCommand.execute(sender, subCommand.mapArguments(args));
    }

    public @NotNull SlashCommandData asCommandData() {
        final SlashCommandData commandData = Commands.slash(name, description);
        final DefaultMemberPermissions memberPermission = allow.isEmpty() ? DefaultMemberPermissions.ENABLED : DefaultMemberPermissions.enabledFor(allow);

        commandData.setDefaultPermissions(memberPermission);

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
                .map(entry -> new SubcommandData(entry.getKey().toLowerCase(), entry.getValue().getDescription()).addOptions(entry.getValue().getJdaOptions()))
                .collect(Collectors.toList());

        commandData.addSubcommands(subData);

        return commandData;
    }

    /**
     * Gets the default sub command or null if there is none.
     *
     * @return The default sub command.
     */
    private @Nullable SlashSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Gets the valid sub command for the given name or null if there is none.
     *
     * @param key The sub command name.
     * @return A sub command or null.
     */
    private @Nullable SlashSubCommand<S> getSubCommand(final @NotNull String key) {
        return subCommands.get(key);
    }
}
