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

import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Listener for the JDA's {@link GuildMessageReceivedEvent}, which triggers the command execution.
 *
 * @param <S> The sender type.
 */
final class SlashCommandListener<S> extends ListenerAdapter {

    private final SlashCommandManager<S> commandManager;
    private final MessageRegistry<S> messageRegistry;
    private final SenderMapper<S, SlashSender> senderMapper;

    public SlashCommandListener(
            @NotNull final SlashCommandManager<S> commandManager,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        this.commandManager = commandManager;
        this.messageRegistry = messageRegistry;
        this.senderMapper = senderMapper;
    }

    @Override
    public void onSlashCommand(@NotNull final SlashCommandEvent event) {
        final String name = event.getName();
        SlashCommand<S> command = commandManager.getCommand(name);
        if (command == null) {
            final Guild guild = event.getGuild();
            if (guild == null) return;
            command = commandManager.getCommand(guild, name);
        }

        if (command == null) return;

        // TODO: 11/27/2021 sender
        final S sender = senderMapper.map(new SlashSender() {
            @Override
            public Guild getGuild() {
                return event.getGuild();
            }

            @Override
            public RestAction reply(final String message) {
                return event.reply(message);
            }
        });
        if (sender == null) return;

        final String subCommandName = event.getSubcommandName();

        final List<String> args = event.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.toList());

        command.execute(sender, subCommandName != null ? subCommandName : Default.DEFAULT_CMD_NAME, args);
    }

    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        commandManager.upsertCommands();
    }
}
