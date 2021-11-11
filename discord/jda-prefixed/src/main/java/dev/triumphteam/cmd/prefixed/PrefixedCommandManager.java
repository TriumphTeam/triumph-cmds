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
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandProcessor;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PrefixedCommandManager extends CommandManager<PrefixedSender> {

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

            for (final String alias : processor.getAlias()) {
                globalCommands.putIfAbsent(alias, commandExecutor);
            }

            commandExecutor.register(processor);
            return;
        }

        final PrefixedCommandExecutor commandExecutor = guildCommands.computeIfAbsent(
                KeyPair.of(guild.getIdLong(), prefix),
                p -> new PrefixedCommandExecutor()
        );

        for (final String alias : processor.getAlias()) {
            guildCommands.putIfAbsent(KeyPair.of(guild.getIdLong(), alias), commandExecutor);
        }

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
