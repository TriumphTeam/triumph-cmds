package dev.triumphteam.cmd.jda;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

final class JdaPrefixedCommandListener extends ListenerAdapter {

    private final JdaPrefixedCommandManager commandSenderCommandManager;

    public JdaPrefixedCommandListener(@NotNull final JdaPrefixedCommandManager commandSenderCommandManager) {
        this.commandSenderCommandManager = commandSenderCommandManager;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {

    }

}
