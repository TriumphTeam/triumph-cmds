package dev.triumphteam.cmd.slash;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

class SlashCommandsListener extends ListenerAdapter {

    private final SlashCommandManager<?> commandManager;

    public SlashCommandsListener(final @NotNull SlashCommandManager<?> commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        commandManager.execute(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull final CommandAutoCompleteInteractionEvent event) {
        commandManager.suggest(event);
    }
}
