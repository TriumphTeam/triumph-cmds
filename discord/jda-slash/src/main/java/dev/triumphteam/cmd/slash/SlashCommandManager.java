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
package dev.triumphteam.cmd.slash;

import com.google.common.collect.Maps;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.AsyncExecutionProvider;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.slash.choices.ChoiceKey;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Command Manager for Slash Commands.
 * Allows for registering of global and guild specific commands.
 * As well the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class SlashCommandManager<S> extends CommandManager<SlashSender, S> {

    private final JDA jda;

    private final SlashRegistryContainer<S> registryContainer = new SlashRegistryContainer<>();

    private final Map<String, SlashCommand<S>> globalCommands = new HashMap<>();
    private final Map<Long, Map<String, SlashCommand<S>>> guildCommands = new HashMap<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    public SlashCommandManager(
            @NotNull final JDA jda,
            @NotNull final SenderMapper<SlashSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
        this.jda = jda;

        jda.addEventListener(new SlashCommandListener<>(this, senderMapper));
    }

    /**
     * Creates a new instance of the {@link SlashCommandManager}.
     * This factory is for adding a custom sender, for default sender use {@link #create(JDA)}.
     *
     * @param jda             The JDA instance created.
     * @param senderMapper    The Mapper to get the custom sender from.
     * @param senderValidator The validator to validate the sender.
     * @param <S>             The type of the custom sender.
     * @return A new instance of the {@link SlashCommandManager}.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static <S> SlashCommandManager<S> create(
            @NotNull final JDA jda,
            @NotNull final SenderMapper<SlashSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        return new SlashCommandManager<>(jda, senderMapper, senderValidator);
    }

    /**
     * Creates a new instance of the {@link SlashCommandManager}.
     * This factory adds all the defaults based on the default sender {@link SlashSender}.
     *
     * @param jda The JDA instance created.
     * @return A new instance of the {@link SlashCommandManager}.
     */
    public static SlashCommandManager<SlashSender> create(@NotNull final JDA jda) {
        final SlashCommandManager<SlashSender> commandManager = create(jda, SenderMapper.defaultMapper(), new SlashSenderValidator());
        setUpDefaults(commandManager);
        return commandManager;
    }

    /**
     * Registers a global command.
     *
     * @param baseCommand The {@link BaseCommand} to be registered.
     */
    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        addCommand(null, baseCommand, Collections.emptyList());
    }

    /**
     * Registers a {@link Guild} command.
     *
     * @param guild       The {@link Guild} to register the command for.
     * @param baseCommand The {@link BaseCommand} to be registered.
     */
    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand baseCommand) {
        addCommand(guild, baseCommand, Collections.emptyList());
    }

    /**
     * Registers a global command for only specific permissions.
     *
     * @param baseCommand  The {@link BaseCommand} to be registered.
     * @param enabledPermissions The {@link Role}s that are allowed to use the command.
     */
    public void registerCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Permission> enabledPermissions) {
        addCommand(null, baseCommand, enabledPermissions);
    }

    /**
     * Registers a {@link Guild} command for only specific permissions.
     *
     * @param guild        The {@link Guild} to register the command for.
     * @param baseCommand  The {@link BaseCommand} to be registered.
     * @param enabledPermissions The {@link Permission}s that are allowed to use the command.
     */
    public void registerCommand(
            @NotNull final Guild guild,
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Permission> enabledPermissions) {
        addCommand(guild, baseCommand, enabledPermissions);
    }

    /**
     * Registers a {@link Guild} command varargs.
     *
     * @param guild        The {@link Guild} to register the command for.
     * @param baseCommands The {@link BaseCommand}s to be registered.
     */
    public void registerCommand(@NotNull final Guild guild, @NotNull final BaseCommand... baseCommands) {
        for (final BaseCommand baseCommand : baseCommands) {
            registerCommand(guild, baseCommand);
        }
    }

    public void registerChoices(@NotNull final ChoiceKey key, @NotNull final Supplier<List<String>> choiceSupplier) {
        registryContainer.getChoiceRegistry().register(key, choiceSupplier);
    }

    @Override
    public void unregisterCommand(final @NotNull BaseCommand command) {
        // TODO: 12/7/2021 Implement some sort of unregistering
    }

    /**
     * Updates all the commands in one go.
     * This should be used if the default trigger for the updating of the commands isn't working.
     * Or if commands are added after the initial setup.
     */
    public void updateAllCommands() {
        jda.updateCommands().addCommands(globalCommands.values().stream().map(SlashCommand::asCommandData).collect(Collectors.toList())).queue();

        guildCommands
                .entrySet()
                .stream()
                .map(entry -> {
                    final Guild guild = jda.getGuildById(entry.getKey());
                    return guild != null ? Maps.immutableEntry(guild, entry.getValue()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach((guild, commands) -> guild.updateCommands()
                        .addCommands(commands.values().stream().map(SlashCommand::asCommandData).collect(Collectors.toList())).queue());
    }

    @NotNull
    @Override
    protected RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    /**
     * Adds a command to the manager.
     *
     * @param guild       The guild to add the command to or null if it's a global command.
     * @param baseCommand The {@link BaseCommand} to be added.
     */
    private void addCommand(
            @Nullable final Guild guild,
            @NotNull final BaseCommand baseCommand,
            @NotNull final List<Permission> enabledPermissions
    ) {
        final SlashCommandProcessor<S> processor = new SlashCommandProcessor<>(
                baseCommand,
                registryContainer,
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider
        );

        final String name = processor.getName();

        final List<Permission> finalEnabledPermissions = new ArrayList<>(enabledPermissions);

        finalEnabledPermissions.addAll(processor.getEnabledPermissions());

        final SlashCommand<S> command;
        if (guild == null) {
            // Global command
            command = globalCommands.computeIfAbsent(name, ignored -> new SlashCommand<>(processor, enabledPermissions, syncExecutionProvider, asyncExecutionProvider));
        } else {
            command = guildCommands
                    .computeIfAbsent(guild.getIdLong(), map -> new HashMap<>())
                    .computeIfAbsent(name, ignored -> new SlashCommand<>(processor, finalEnabledPermissions, syncExecutionProvider, asyncExecutionProvider));
        }

        command.addSubCommands(processor.getSubCommands(), processor.getSubCommandsAlias());
    }

    /**
     * Gets the {@link SlashCommand} for the given name.
     *
     * @param name The name of the command.
     * @return The {@link SlashCommand} or null if it doesn't exist.
     */
    @Nullable
    SlashCommand<S> getCommand(@NotNull final String name) {
        return globalCommands.get(name);
    }

    /**
     * Gets the {@link SlashCommand} for the given name and guild.
     *
     * @param guild The guild to get the command from.
     * @param name  The name of the command.
     * @return The {@link SlashCommand} or null if it doesn't exist.
     */
    @Nullable
    SlashCommand<S> getCommand(@NotNull Guild guild, @NotNull final String name) {
        final Map<String, SlashCommand<S>> commands = guildCommands.get(guild.getIdLong());
        return commands != null ? commands.get(name) : null;
    }

    /**
     * Sets up all the default values for the default sender on the platform.
     *
     * @param manager The {@link CommandManager} to use.
     */
    private static void setUpDefaults(@NotNull final SlashCommandManager<SlashSender> manager) {
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.reply("Unknown command: `" + context.getCommand() + "`.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.reply("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").setEphemeral(true).queue());

        manager.registerArgument(Member.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getMemberById(arg);
        });
        manager.registerArgument(User.class, (sender, arg) -> sender.getEvent().getJDA().retrieveUserById(arg));
        manager.registerArgument(TextChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getTextChannelById(arg);
        });
        manager.registerArgument(VoiceChannel.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getVoiceChannelById(arg);
        });
        manager.registerArgument(Role.class, (sender, arg) -> {
            final Guild guild = sender.getGuild();
            if (guild == null) return null;
            return guild.getRoleById(arg);
        });
    }

}
