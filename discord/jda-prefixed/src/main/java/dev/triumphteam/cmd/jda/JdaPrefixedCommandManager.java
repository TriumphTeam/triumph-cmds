package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.jda.sender.PrefixedCommandSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class JdaPrefixedCommandManager extends CommandManager<PrefixedCommandSender> {

    private final Map<String, JdaPrefixedCommandExecutor> globalCommands = new HashMap<>();
    private final Map<KeyPair<Guild, String>, JdaPrefixedCommandExecutor> guildCommands = new HashMap<>();

    private final JDA jda;
    private final JdaPrefixedCommandListener jdaCommandListener = new JdaPrefixedCommandListener(this);

    public JdaPrefixedCommandManager(@NotNull final JDA jda) {
        this.jda = jda;

        jda.addEventListener(jdaCommandListener);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand command) {

    }

    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {

    }

    @Nullable
    JdaPrefixedCommandExecutor getGuildCommand(@NotNull final KeyPair<Guild, String> key) {
        return guildCommands.get(key);
    }

    @Nullable
    JdaPrefixedCommandExecutor getGlobalCommand(@NotNull final String key) {
        return globalCommands.get(key);
    }

}
