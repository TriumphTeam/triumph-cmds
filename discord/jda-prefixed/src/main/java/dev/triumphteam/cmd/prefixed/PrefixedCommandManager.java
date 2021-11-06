package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.prefixed.command.PrefixedCommand;
import dev.triumphteam.cmd.prefixed.sender.PrefixedCommandSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class PrefixedCommandManager extends CommandManager<PrefixedCommandSender> {

    private final Map<String, PrefixedCommandExecutor> globalCommands = new HashMap<>();
    private final Map<KeyPair<Guild, String>, PrefixedCommandExecutor> guildCommands = new HashMap<>();

    private final PrefixedCommandListener jdaCommandListener = new PrefixedCommandListener(this);

    public PrefixedCommandManager(@NotNull final JDA jda) {
        jda.addEventListener(jdaCommandListener);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final PrefixedCommand prefixedCommand;
    }

    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand command) {

    }

    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand... baseCommands) {
        for (final BaseCommand command : baseCommands) {
            registerCommand(guild, command);
        }
    }

    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {

    }

    @Nullable
    PrefixedCommandExecutor getGuildCommand(@NotNull final KeyPair<Guild, String> key) {
        return guildCommands.get(key);
    }

    @Nullable
    PrefixedCommandExecutor getGlobalCommand(@NotNull final String key) {
        return globalCommands.get(key);
    }

}
