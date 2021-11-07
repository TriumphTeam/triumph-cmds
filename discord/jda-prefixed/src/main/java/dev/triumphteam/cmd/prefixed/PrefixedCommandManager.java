package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandProcessor;
import dev.triumphteam.cmd.prefixed.sender.PrefixedCommandSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PrefixedCommandManager extends CommandManager<PrefixedCommandSender> {

    private final Set<String> prefixes = new HashSet<>();
    private final Map<String, PrefixedCommandExecutor> globalCommands = new HashMap<>();
    private final Map<KeyPair<Long, String>, PrefixedCommandExecutor> guildCommands = new HashMap<>();

    private final String globalPrefix;
    private final PrefixedCommandListener jdaCommandListener = new PrefixedCommandListener(this);

    public PrefixedCommandManager(@NotNull final JDA jda, @NotNull final String globalPrefix) {
        this.globalPrefix = globalPrefix;
        jda.addEventListener(jdaCommandListener);
    }

    public PrefixedCommandManager(@NotNull final JDA jda) {
        this(jda, "");
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        addCommand(null, baseCommand);
    }

    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand baseCommand) {
    }

    private void addCommand(@Nullable final Guild guild, @NotNull final BaseCommand baseCommand) {
        final PrefixedCommandProcessor processor = new PrefixedCommandProcessor(
                baseCommand,
                getArgumentRegistry(),
                getRequirementRegistry(),
                getMessageRegistry()
        );

        String prefix = processor.getPrefix();
        if (prefix.isEmpty()) {
            if (globalPrefix.isEmpty()) {
                throw new CommandRegistrationException("TODO");
            }

            prefix = globalPrefix;
        }

        prefixes.add(prefix);

        if (guild == null) {
            final PrefixedCommandExecutor commandExecutor = globalCommands.computeIfAbsent(
                    prefix,
                    p -> new PrefixedCommandExecutor()
            );

            commandExecutor.register(processor);
            return;
        }

        final PrefixedCommandExecutor commandExecutor = guildCommands.computeIfAbsent(
                KeyPair.of(guild.getIdLong(), prefix),
                p -> new PrefixedCommandExecutor()
        );
        commandExecutor.register(processor);
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
    PrefixedCommandExecutor getGuildCommand(@NotNull final Guild guild, @NotNull final String prefix) {
        return guildCommands.get(KeyPair.of(guild.getIdLong(), prefix));
    }

    @Nullable
    PrefixedCommandExecutor getGlobalCommand(@NotNull final String key) {
        return globalCommands.get(key);
    }

    @NotNull
    Set<String> getPrefixes() {
        return prefixes;
    }

}
