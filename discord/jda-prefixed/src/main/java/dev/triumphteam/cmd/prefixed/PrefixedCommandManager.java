/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.extention.sender.SenderValidator;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Command Manager for Prefixed Commands.
 * Allows for registering of global and guild specific commands.
 * As well the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class PrefixedCommandManager<S> extends CommandManager<PrefixedSender, S> {

    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("<@!?(?<id>\\d+)>");
    private static final Pattern ROLE_MENTION_PATTERN = Pattern.compile("<@&(?<id>\\d+)>");
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(?<id>\\d+)>");
    private static final Pattern USER_TAG_PATTERN = Pattern.compile(".{3,32}#\\d{4}");

    private final RegistryContainer<PrefixedSender, S> registryContainer;

    private final Set<String> prefixes = new HashSet<>();
    private final Set<Pattern> prefixesRegexes = new HashSet<>();

    private final Map<String, PrefixedCommandHolder<S>> globalCommands = new HashMap<>();
    private final Map<Long, Map<String, PrefixedCommandHolder<S>>> guildCommands = new HashMap<>();

    private final String globalPrefix;

    private PrefixedCommandManager(
            final @NotNull JDA jda,
            final @NotNull PrefixedCommandOptions<S> commandOptions,
            final @NotNull RegistryContainer<PrefixedSender, S> registryContainer
    ) {
        super(commandOptions);

        this.registryContainer = registryContainer;
        this.globalPrefix = commandOptions.getGlobalPrefix();
    }

    @Contract("_, _, _, -> new")
    public static <S> @NotNull PrefixedCommandManager<S> create(
            final @NotNull JDA jda,
            final @NotNull SenderExtension<PrefixedSender, S> senderExtension,
            final @NotNull Consumer<PrefixedCommandOptions.Builder<S>> builder
    ) {
        final RegistryContainer<PrefixedSender, S> registryContainer = new RegistryContainer<>();
        final PrefixedCommandOptions.Builder<S> extensionBuilder = new PrefixedCommandOptions.Builder<>(registryContainer);
        builder.accept(extensionBuilder);
        return new PrefixedCommandManager<>(jda, extensionBuilder.build(senderExtension), registryContainer);
    }

    @Contract("_ -> new")
    public static @NotNull PrefixedCommandManager<PrefixedSender> create(final @NotNull JDA jda) {
        return create(jda, new PrefixedSenderExtension(), builder -> {});
    }


    @Override
    public void registerCommand(final @NotNull Object command) {
        // TODO
    }

    public void registerCommand(final @NotNull Guild guild, final @NotNull Object command) {
        // TODO
    }

    @Override
    public void unregisterCommand(final @NotNull Object command) {

    }

    @Override
    protected @NotNull RegistryContainer<PrefixedSender, S> getRegistryContainer() {
        return registryContainer;
    }

    private void addCommand(final @Nullable Guild guild, final @NotNull Object command) {
        final RootCommandProcessor<PrefixedSender, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add command, then add its sub commands
        final PrefixedCommandHolder<S> holder = globalCommands.computeIfAbsent(name, it -> new RootCommand<>(processor));
        final RootCommand<CommandSender, S> rootCommand = bukkitCommand.getRootCommand();
        rootCommand.addCommands(command, processor.commands(rootCommand));

        String prefix = processor.getPrefix();
        if (prefix.isEmpty()) {
            if (globalPrefix.isEmpty()) {
                throw new CommandRegistrationException("The command prefix cannot be empty.", baseCommand.getClass());
            }

            prefix = globalPrefix;
        }

        prefixes.add(prefix);
        // TODO: 11/26/2021 Join into a map
        prefixesRegexes.add(Pattern.compile("^(?<prefix>" + Pattern.quote(prefix) + ")[\\w]"));

        // Global command
        if (guild == null) {
            final PrefixedCommandHolder<S> commandExecutor = globalCommands.computeIfAbsent(
                    prefix,
                    ignored -> new PrefixedCommandHolder<>(registryContainer.getMessageRegistry(), syncExecutionProvider, asyncExecutionProvider)
            );

            for (final String alias : processor.getAlias()) {
                globalCommands.putIfAbsent(alias, commandExecutor);
            }

            commandExecutor.register(processor);
            return;
        }

        // Guild command
        final PrefixedCommandHolder<S> commandExecutor = guildCommands
                .computeIfAbsent(guild.getIdLong(), ignored -> new HashMap<>())
                .computeIfAbsent(
                        prefix,
                        ignored -> new PrefixedCommandHolder<>(
                                registryContainer.getMessageRegistry(),
                                syncExecutionProvider,
                                asyncExecutionProvider
                        )
                );

        for (final String alias : processor.getAlias()) {
            // TODO: 12/7/2021 Alias need rework
            //guildCommands.putIfAbsent(Pair.of(guild.getIdLong(), alias), commandExecutor);
        }

        commandExecutor.register(processor);
    }
}
