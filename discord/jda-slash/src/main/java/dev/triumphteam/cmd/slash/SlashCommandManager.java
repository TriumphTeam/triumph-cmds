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
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.AsyncExecutionProvider;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestiblePlatform;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import dev.triumphteam.cmd.core.util.Pair;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Command Manager for Slash Commands.
 * Allows for registering of global and guild specific commands.
 * As well the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class SlashCommandManager<S> extends CommandManager<S> implements SuggestiblePlatform {

    private final JDA jda;

    private final Map<String, SlashCommand<S>> globalCommands = new HashMap<>();
    private final Map<Long, Map<String, SlashCommand<S>>> guildCommands = new HashMap<>();
    private final SuggestionRegistry suggestionRegistry = new SuggestionRegistry();

    private final SenderMapper<S, SlashSender> senderMapper;

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    public SlashCommandManager(
            @NotNull final JDA jda,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        this.jda = jda;
        this.senderMapper = senderMapper;

        jda.addEventListener(new SlashCommandListener<>(this, senderMapper));
    }

    public static SlashCommandManager<SlashSender> createDefault(
            @NotNull final JDA jda
    ) {
        final SlashCommandManager<SlashSender> commandManager = new SlashCommandManager<>(jda, new SlashSenderMapper());
        setUpDefaults(commandManager);
        return commandManager;
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        addCommand(null, baseCommand, Collections.emptyList(), Collections.emptyList());
    }

    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand baseCommand) {
        addCommand(guild, baseCommand, Collections.emptyList(), Collections.emptyList());
    }

    public void registerCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Long> enabledRoles,
            @NotNull final List<Long> disabledRoles
    ) {
        addCommand(null, baseCommand, enabledRoles, disabledRoles);
    }

    public void registerCommand(
            @NotNull final Guild guild,
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Long> enabledRoles,
            @NotNull final List<Long> disabledRoles
    ) {
        addCommand(guild, baseCommand, enabledRoles, disabledRoles);
    }

    @Override
    public void registerSuggestion(@NotNull final SuggestionKey key, @NotNull final SuggestionResolver suggestionResolver) {
        suggestionRegistry.register(key, suggestionResolver);
    }

    @Override
    public void unregisterCommand(final @NotNull BaseCommand command) {

    }

    public void upsertCommands() {
        jda.updateCommands().addCommands(globalCommands.values().stream().map(SlashCommand::asCommandData).collect(Collectors.toList())).queue();

        guildCommands
                .entrySet()
                .stream()
                .map(entry -> {
                    final Guild guild = jda.getGuildById(entry.getKey());
                    return guild != null ? Pair.of(guild, entry.getValue()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
                .forEach((guild, commands) -> guild.updateCommands()
                        .addCommands(commands.values().stream().map(SlashCommand::asCommandData).collect(Collectors.toList()))
                        .queue(cmds -> guild.updateCommandPrivileges(
                                cmds.stream()
                                        .map(cmd -> {
                                            final SlashCommand<S> command = commands.get(cmd.getName());
                                            if (command == null) return null;

                                            final List<CommandPrivilege> privileges = Stream.concat(
                                                    command.getEnabledRoles().stream().map(id -> new CommandPrivilege(CommandPrivilege.Type.ROLE, true, id)),
                                                    command.getDisabledRoles().stream().map(id -> new CommandPrivilege(CommandPrivilege.Type.ROLE, false, id))
                                            ).collect(Collectors.toList());

                                            if (privileges.isEmpty()) return null;
                                            return Pair.of(cmd.getId(), privileges);
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
                        ).queue()));
    }

    /**
     * Adds a command to the manager.
     *
     * @param guild       The guild to add the command to or null if it's a global command.
     * @param baseCommand The {@link BaseCommand} to be added.
     */
    private void addCommand(
            @Nullable final Guild guild,
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Long> enabledRoles,
            @NotNull final List<Long> disabledRoles
    ) {
        final SlashCommandProcessor<S> processor = new SlashCommandProcessor<>(
                baseCommand,
                getArgumentRegistry(),
                getRequirementRegistry(),
                getMessageRegistry(),
                suggestionRegistry,
                senderMapper
        );

        final String name = processor.getName();

        final List<Long> finalEnabledRoles = new ArrayList<>(enabledRoles);
        final List<Long> finalDisabledRoles = new ArrayList<>(disabledRoles);

        finalEnabledRoles.addAll(processor.getEnabledRoles());
        finalDisabledRoles.addAll(processor.getDisabledRoles());

        final SlashCommand<S> command;
        if (guild == null) {
            // Global command
            command = globalCommands.computeIfAbsent(name, ignored -> new SlashCommand<>(processor, finalEnabledRoles, finalDisabledRoles, syncExecutionProvider, asyncExecutionProvider));
        } else {
            command = guildCommands
                    .computeIfAbsent(guild.getIdLong(), map -> new HashMap<>())
                    .computeIfAbsent(name, ignored -> new SlashCommand<>(processor, finalEnabledRoles, finalDisabledRoles, syncExecutionProvider, asyncExecutionProvider));
        }

        command.addSubCommands(baseCommand);
    }

    @Nullable
    SlashCommand<S> getCommand(@NotNull final String name) {
        return globalCommands.get(name);
    }

    @Nullable
    SlashCommand<S> getCommand(@NotNull Guild guild, @NotNull final String name) {
        final Map<String, SlashCommand<S>> commands = guildCommands.get(guild.getIdLong());
        return commands != null ? commands.get(name) : null;
    }

    private static void setUpDefaults(@NotNull final SlashCommandManager<SlashSender> manager) {
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.reply("Unknown command: `" + context.getCommand() + "`.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.reply("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG, (sender, context) -> sender.reply("Command is missing required flags.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT, (sender, context) -> sender.reply("Command is missing required flags argument.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.INVALID_FLAG_ARGUMENT, (sender, context) -> sender.reply("Invalid flag argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").setEphemeral(true).queue());

        manager.registerArgument(Member.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getMemberById(arg);
        });
        manager.registerArgument(User.class, (sender, arg) -> sender.getEvent().getJDA().getUserById(arg));
        manager.registerArgument(TextChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getTextChannelById(arg);
        });
        manager.registerArgument(VoiceChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getVoiceChannelById(arg);
        });
        manager.registerArgument(Role.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getRoleById(arg);
        });
    }

}
