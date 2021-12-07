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

import com.google.common.primitives.Longs;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.execution.AsyncExecutionProvider;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.util.Pair;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command Manager for Prefixed Commands.
 * Allows for registering of global and guild specific commands.
 * As well the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class PrefixedCommandManager<S> extends CommandManager<S> {

    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("<@!?(?<id>\\d+)>");
    private static final Pattern ROLE_MENTION_PATTERN = Pattern.compile("<@&(?<id>\\d+)>");
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(?<id>\\d+)>");
    private static final Pattern USER_TAG_PATTERN = Pattern.compile(".{3,32}#\\d{4}");

    private final Set<String> prefixes = new HashSet<>();
    private final Set<Pattern> prefixesRegexes = new HashSet<>();
    private final Map<String, PrefixedCommandExecutor<S>> globalCommands = new HashMap<>();
    private final Map<Pair<Long, String>, PrefixedCommandExecutor<S>> guildCommands = new HashMap<>();

    private final String globalPrefix;
    private final SenderMapper<S, PrefixedSender> senderMapper;

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    private PrefixedCommandManager(
            @NotNull final JDA jda,
            @NotNull final String globalPrefix,
            @NotNull final SenderMapper<S, PrefixedSender> senderMapper
    ) {
        this.globalPrefix = globalPrefix;
        this.senderMapper = senderMapper;

        jda.addEventListener(new PrefixedCommandListener<>(this, getMessageRegistry(), senderMapper));
    }

    /**
     * Creates a new instance of the PrefixedCommandManager.
     * This constructor is for adding a custom sender, for default sender use {@link #createDefault(JDA, String)}.
     *
     * @param jda          The JDA instance.
     * @param globalPrefix The global prefix.
     * @param senderMapper The sender mapper.
     * @param <S>          The sender type.
     * @return The new instance.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static <S> PrefixedCommandManager<S> create(
            @NotNull final JDA jda,
            @NotNull final String globalPrefix,
            @NotNull final SenderMapper<S, PrefixedSender> senderMapper) {
        return new PrefixedCommandManager<>(jda, globalPrefix, senderMapper);
    }

    /**
     * Creates a new instance of the PrefixedCommandManager.
     * This constructor is for adding a custom sender, for default sender use {@link #createDefault(JDA)}.
     *
     * @param jda          The JDA instance.
     * @param senderMapper The sender mapper.
     * @param <S>          The sender type.
     * @return The new instance.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static <S> PrefixedCommandManager<S> create(
            @NotNull final JDA jda,
            @NotNull final SenderMapper<S, PrefixedSender> senderMapper
    ) {
        return create(jda, "", senderMapper);
    }

    /**
     * Creates a new instance of the PrefixedCommandManager with its default sender.
     *
     * @param jda          The JDA instance.
     * @param globalPrefix The global prefix.
     * @return The new instance.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static PrefixedCommandManager<PrefixedSender> createDefault(@NotNull final JDA jda, @NotNull final String globalPrefix) {
        final PrefixedCommandManager<PrefixedSender> manager = new PrefixedCommandManager<>(jda, globalPrefix, new PrefixedSenderMapper());
        setUpDefaults(manager);
        return manager;
    }

    /**
     * Creates a new instance of the PrefixedCommandManager with its default sender.
     *
     * @param jda The JDA instance.
     * @return The new instance.
     */
    @NotNull
    @Contract("_, -> new")
    public static PrefixedCommandManager<PrefixedSender> createDefault(@NotNull final JDA jda) {
        return createDefault(jda, "");
    }

    /**
     * Registers a global command.
     *
     * @param baseCommand The {@link BaseCommand} to be registered.
     */
    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        addCommand(null, baseCommand);
    }

    /**
     * Registers a {@link Guild} command.
     *
     * @param guild       the {@link Guild} to register the command to.
     * @param baseCommand The {@link BaseCommand} to be registered.
     */
    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand baseCommand) {
        addCommand(guild, baseCommand);
    }

    /**
     * Registers a list of guild {@link BaseCommand}s.
     *
     * @param guild        The {@link Guild} to register the commands for.
     * @param baseCommands A list of baseCommands to be registered.
     */
    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand... baseCommands) {
        for (final BaseCommand command : baseCommands) {
            registerCommand(guild, command);
        }
    }

    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {
        // TODO: 11/23/2021 Add unregistering commands and also guild commands
    }

    /**
     * Adds a command to the manager.
     *
     * @param guild       The guild to add the command to or null if it's a global command.
     * @param baseCommand The {@link BaseCommand} to be added.
     */
    private void addCommand(@Nullable final Guild guild, @NotNull final BaseCommand baseCommand) {
        final PrefixedCommandProcessor<S> processor = new PrefixedCommandProcessor<>(
                baseCommand,
                getArgumentRegistry(),
                getRequirementRegistry(),
                getMessageRegistry(),
                senderMapper
        );

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
            final PrefixedCommandExecutor<S> commandExecutor = globalCommands.computeIfAbsent(
                    prefix,
                    ignored -> new PrefixedCommandExecutor<>(getMessageRegistry(), syncExecutionProvider, asyncExecutionProvider)
            );

            for (final String alias : processor.getAlias()) {
                globalCommands.putIfAbsent(alias, commandExecutor);
            }

            commandExecutor.register(processor);
            return;
        }

        // Guild command
        final PrefixedCommandExecutor<S> commandExecutor = guildCommands.computeIfAbsent(
                Pair.of(guild.getIdLong(), prefix),
                ignored -> new PrefixedCommandExecutor<>(getMessageRegistry(), syncExecutionProvider, asyncExecutionProvider)
        );

        for (final String alias : processor.getAlias()) {
            guildCommands.putIfAbsent(Pair.of(guild.getIdLong(), alias), commandExecutor);
        }

        commandExecutor.register(processor);
    }

    /**
     * Gets a guild command.
     *
     * @param guild  The {@link Guild} to get the command from.
     * @param prefix The prefix of the command.
     * @return The {@link BaseCommand} or null if it doesn't exist.
     */
    @Nullable
    PrefixedCommandExecutor<S> getCommand(@NotNull final Guild guild, @NotNull final String prefix) {
        return guildCommands.get(Pair.of(guild.getIdLong(), prefix));
    }

    /**
     * Gets a global command.
     *
     * @param prefix The prefix of the command.
     * @return The {@link BaseCommand} or null if it doesn't exist.
     */
    @Nullable
    PrefixedCommandExecutor<S> getCommand(@NotNull final String prefix) {
        return globalCommands.get(prefix);
    }

    /**
     * Gets a {@link Set} with all the registered prefixes.
     *
     * @return A {@link Set} with all the registered prefixes.
     */
    @NotNull
    Set<String> getPrefixes() {
        return prefixes;
    }

    /**
     * Gets a {@link Set} with all registered prefixes regexes.
     *
     * @return A {@link Set} with all the registered prefixes regexes.
     */
    @NotNull
    Set<Pattern> getPrefixesRegexes() {
        return prefixesRegexes;
    }

    private static void setUpDefaults(@NotNull final PrefixedCommandManager<PrefixedSender> manager) {
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.getMessage().reply("Unknown command: `" + context.getCommand() + "`.").queue());
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.getMessage().reply("Invalid usage.").queue());
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.getMessage().reply("Invalid usage.").queue());
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.getMessage().reply("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").queue());
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG, (sender, context) -> sender.getMessage().reply("Command is missing required flags.").queue());
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT, (sender, context) -> sender.getMessage().reply("Command is missing required flags argument.").queue());
        manager.registerMessage(MessageKey.INVALID_FLAG_ARGUMENT, (sender, context) -> sender.getMessage().reply("Invalid flag argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").queue());

        manager.registerArgument(User.class, (sender, arg) -> {
            final JDA jda = sender.getJDA();
            final Long id = Longs.tryParse(arg);
            if (id != null) return jda.getUserById(id);

            if (USER_TAG_PATTERN.matcher(arg).matches()) return jda.getUserByTag(arg);

            final Matcher userMentionMatcher = USER_MENTION_PATTERN.matcher(arg);
            return userMentionMatcher.matches() ? jda.getUserById(userMentionMatcher.group("id")) : null;
        });
        manager.registerArgument(Member.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            final Long id = Longs.tryParse(arg);
            if (id != null) return guild.getMemberById(id);

            if (USER_TAG_PATTERN.matcher(arg).matches()) return guild.getMemberByTag(arg);

            final Matcher userMentionMatcher = USER_MENTION_PATTERN.matcher(arg);
            return userMentionMatcher.matches() ? guild.getMemberById(userMentionMatcher.group("id")) : null;
        });
        manager.registerArgument(TextChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            final Long id = Longs.tryParse(arg);
            if (id != null) return guild.getTextChannelById(id);

            final Matcher channelMentionMatcher = CHANNEL_MENTION_PATTERN.matcher(arg);
            return channelMentionMatcher.matches() ? guild.getTextChannelById(channelMentionMatcher.group("id")) : null;
        });
        manager.registerArgument(VoiceChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            final Long id = Longs.tryParse(arg);
            if (id != null) return guild.getVoiceChannelById(id);

            final Matcher channelMentionMatcher = CHANNEL_MENTION_PATTERN.matcher(arg);
            return channelMentionMatcher.matches() ? guild.getVoiceChannelById(channelMentionMatcher.group("id")) : null;
        });
        manager.registerArgument(Role.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            final Long id = Longs.tryParse(arg);
            if (id != null) return guild.getRoleById(id);

            final Matcher roleMentionMatcher = ROLE_MENTION_PATTERN.matcher(arg);
            return roleMentionMatcher.matches() ? guild.getRoleById(roleMentionMatcher.group("id")) : null;
        });
    }

}
