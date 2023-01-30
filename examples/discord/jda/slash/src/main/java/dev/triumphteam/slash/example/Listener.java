package dev.triumphteam.slash.example;

import dev.triumphteam.cmd.slash.SlashCommandManager;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter {

    private final SlashCommandManager<SlashSender> commandManager;

    public Listener(final @NotNull SlashCommandManager<SlashSender> commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        commandManager.execute(event);
    }
}
