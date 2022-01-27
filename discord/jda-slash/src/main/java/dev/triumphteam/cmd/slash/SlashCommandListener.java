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

import com.google.common.collect.Maps;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Listener for handling slash command registration and execution.
 *
 * @param <S> The sender type.
 */
final class SlashCommandListener<S> extends ListenerAdapter {

    private final SlashCommandManager<S> commandManager;
    private final SenderMapper<S, SlashSender> senderMapper;

    public SlashCommandListener(
            @NotNull final SlashCommandManager<S> commandManager,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        this.commandManager = commandManager;
        this.senderMapper = senderMapper;
    }

    /**
     * Handler for the slash commands.
     * Needs to map the given result to the correct arguments to be used.
     *
     * @param event The slash command event.
     */
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

        final S sender = senderMapper.map(new SlashCommandSender(event));
        if (sender == null) return;

        final String subCommandName = event.getSubcommandName();

        final Map<String, String> args = event.getOptions()
                .stream()
                .map(it -> Maps.immutableEntry(it.getName(), it.getAsString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        command.execute(sender, subCommandName != null ? subCommandName : Default.DEFAULT_CMD_NAME, args);
    }

    /**
     * Updates all the commands on ready.
     *
     * @param event The ready event.
     */
    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        commandManager.updateAllCommands();
    }
}
