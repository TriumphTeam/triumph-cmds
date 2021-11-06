package dev.triumphteam.cmd.prefixed;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

final class PrefixedCommandListener extends ListenerAdapter {

    private final PrefixedCommandManager commandSenderCommandManager;

    public PrefixedCommandListener(@NotNull final PrefixedCommandManager commandSenderCommandManager) {
        this.commandSenderCommandManager = commandSenderCommandManager;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {

    }

}
