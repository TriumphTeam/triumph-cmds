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
import dev.triumphteam.cmd.core.util.Pair;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Command Manager for Slash Commands.
 * Allows for registering of global and guild specific commands.
 * As well the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class SlashCommandManager<S> extends CommandManager<S> {

    private final JDA jda;

    private final Map<String, SlashCommand<S>> globalCommands = new HashMap<>();
    private final Map<Pair<Long, String>, SlashCommand<S>> guildCommands = new HashMap<>();

    private final SenderMapper<S, SlashSender> senderMapper;

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    public SlashCommandManager(
            @NotNull final JDA jda,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        this.jda = jda;
        this.senderMapper = senderMapper;

        jda.addEventListener(new SlashCommandListener<>(this, getMessageRegistry(), senderMapper));
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
        addCommand(null, baseCommand);
    }

    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand baseCommand) {
        addCommand(guild, baseCommand);
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
                    final Guild guild = jda.getGuildById(entry.getKey().getFirst());
                    return guild != null ? Pair.of(guild, entry.getValue().asCommandData()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                .forEach((guild, commands) -> guild.updateCommands().addCommands(commands).queue());
    }

    /**
     * Adds a command to the manager.
     *
     * @param guild       The guild to add the command to or null if it's a global command.
     * @param baseCommand The {@link BaseCommand} to be added.
     */
    private void addCommand(@Nullable final Guild guild, @NotNull final BaseCommand baseCommand) {
        final SlashCommandProcessor<S> processor = new SlashCommandProcessor<>(
                baseCommand,
                getArgumentRegistry(),
                getRequirementRegistry(),
                getMessageRegistry(),
                senderMapper
        );

        final String name = processor.getName();

        // Global command
        if (guild == null) {
            final SlashCommand<S> command = globalCommands.computeIfAbsent(name, ignored -> new SlashCommand<>(processor, syncExecutionProvider, asyncExecutionProvider));
            command.addSubCommands(baseCommand);
            return;
        }

        final SlashCommand<S> command = guildCommands.computeIfAbsent(Pair.of(guild.getIdLong(), name), ignored -> new SlashCommand<>(processor, syncExecutionProvider, asyncExecutionProvider));
        command.addSubCommands(baseCommand);
    }

    @Nullable
    SlashCommand<S> getCommand(@NotNull final String name) {
        return globalCommands.get(name);
    }

    @Nullable
    SlashCommand<S> getCommand(@NotNull Guild guild, @NotNull final String name) {
        return guildCommands.get(Pair.of(guild.getIdLong(), name));
    }

    Map<String, SlashCommand<S>> getGlobalCommands() {
        return globalCommands;
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
