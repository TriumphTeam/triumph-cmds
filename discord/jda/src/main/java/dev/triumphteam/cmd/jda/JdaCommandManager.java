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
package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import dev.triumphteam.cmd.discord.LeafResult;
import dev.triumphteam.cmd.discord.ProvidedInternalArgument;
import dev.triumphteam.cmd.discord.annotation.Defer;
import dev.triumphteam.cmd.jda.sender.Sender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.triumphteam.cmd.discord.DiscordCommandUtil.findExecutable;

/**
 * Command Manager for Slash Commands.
 * Allows for registering of global and guild-specific commands.
 * As well as the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class JdaCommandManager<S> extends CommandManager<Sender, S, JdaCommandOptions<S>, Command.Choice> {

    private final JDA jda;

    private final Map<String, InternalRootCommand<Sender, S, Command.Choice>> globalCommands = new HashMap<>();
    private final Map<Long, Map<String, InternalRootCommand<Sender, S, Command.Choice>>> guildCommands = new HashMap<>();

    private JdaCommandManager(
            final @NotNull JDA jda,
            final @NotNull JdaCommandOptions<S> commandOptions,
            final @NotNull RegistryContainer<Sender, S, Command.Choice> registryContainer
    ) {
        super(commandOptions, registryContainer);
        this.jda = jda;

        // All use the same factory
        final InternalArgument.Factory<S, Command.Choice> jdaArgumentFactory = ProvidedInternalArgument::new;

        registerArgument(User.class, ProvidedUserInternalArgument::new);
        registerArgument(Role.class, jdaArgumentFactory);
        registerArgument(Member.class, jdaArgumentFactory);
        registerArgument(GuildChannel.class, jdaArgumentFactory);
        registerArgument(TextChannel.class, jdaArgumentFactory);
        registerArgument(MessageChannel.class, jdaArgumentFactory);
        registerArgument(Message.Attachment.class, jdaArgumentFactory);
        registerArgument(IMentionable.class, jdaArgumentFactory);

        if (commandOptions.autoRegisterListener()) {
            try {
                jda.awaitReady();
                jda.addEventListener(new JdaCommandsListener(this));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates a new instance of {@link JdaCommandManager} with the provided parameters.
     *
     * @param jda             The JDA instance used to register commands and handle interactions.
     * @param senderExtension The extension to handle sender mapping and validation.
     * @param builder         A consumer to configure additional options for the command manager.
     * @param <S>             The sender type that is tied to the command execution.
     * @return A new instance of {@link JdaCommandManager} configured with the provided parameters.
     */
    @Contract("_, _, _ -> new")
    public static <S> @NotNull JdaCommandManager<S> create(
            final @NotNull JDA jda,
            final @NotNull SenderExtension<Sender, S> senderExtension,
            final @NotNull Consumer<JdaCommandOptions.Builder<S>> builder
    ) {
        final RegistryContainer<Sender, S, Command.Choice> registryContainer = new RegistryContainer<>();
        final JdaCommandOptions.Builder<S> extensionBuilder = new JdaCommandOptions.Builder<>();
        builder.accept(extensionBuilder);
        return new JdaCommandManager<>(jda, extensionBuilder.build(senderExtension), registryContainer);
    }

    /**
     * Creates a new instance of {@link JdaCommandManager} with the provided JDA instance and configuration options.
     *
     * @param jda     The JDA instance used to register commands and handle interactions.
     * @param builder A consumer to configure additional options for the command manager.
     * @return A new instance of {@link JdaCommandManager} configured with the provided parameters.
     */
    @Contract("_, _ -> new")
    public static @NotNull JdaCommandManager<Sender> create(
            final @NotNull JDA jda,
            final @NotNull Consumer<JdaCommandOptions.Builder<Sender>> builder
    ) {
        final RegistryContainer<Sender, Sender, Command.Choice> registryContainer = new RegistryContainer<>();
        final JdaCommandOptions.Builder<Sender> extensionBuilder = new JdaCommandOptions.Builder<>();

        // Setup defaults for Bukkit
        final MessageRegistry<Sender> messageRegistry = registryContainer.getMessageRegistry();
        setUpDefaults(messageRegistry);

        // Then accept configured values
        builder.accept(extensionBuilder);
        return new JdaCommandManager<>(jda, extensionBuilder.build(new JdaSenderExtension()), registryContainer);
    }

    /**
     * Creates a new instance of {@link JdaCommandManager} with the provided JDA instance.
     *
     * @param jda The JDA instance used to register commands and handle interactions.
     * @return A new instance of {@link JdaCommandManager} configured with default settings.
     */
    @Contract("_ -> new")
    public static @NotNull JdaCommandManager<Sender> create(final @NotNull JDA jda) {
        return create(jda, builder -> {});
    }

    private static void setUpDefaults(final @NotNull MessageRegistry<Sender> registry) {
        registry.register(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.reply("Unknown command: `" + context.getInvalidInput() + "`.").setEphemeral(true).queue());
        registry.register(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        registry.register(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        registry.register(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.reply("Invalid argument `" + context.getInvalidInput() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").setEphemeral(true).queue());
    }

    public void execute(final @NotNull SlashCommandInteractionEvent event) {
        final Deque<String> commands = new ArrayDeque<>(Arrays.asList(event.getFullCommandName().split(" ")));

        final SenderExtension<Sender, S> senderExtension = getCommandOptions().getCommandExtensions().getSenderExtension();
        final S sender = senderExtension.map(new InteractionCommandSender(event));

        final LeafResult<Sender, S, Command.Choice> result = findExecutable(sender, getAppropriateMap(event), commands, true);
        if (result == null) return;

        // Mapping of arguments
        final Map<String, ArgumentInput> arguments = new HashMap<>();
        for (final OptionMapping option : event.getOptions()) {
            arguments.put(option.getName(), JdaMappingUtil.parsedValueFromType(option));
        }

        final InternalLeafCommand<Sender, S, Command.Choice> command = result.getCommand();

        try {
            // Check if the command was marked to be deferred.
            if (command.getMeta().getOrDefault(Defer.META_KEY, false)) {
                event.deferReply().queue();
            }

            command.execute(sender, result.getInstanceSupplier(), arguments);
        } catch (final @NotNull Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
    }

    public void suggest(final @NotNull CommandAutoCompleteInteractionEvent event) {
        final Deque<String> commands = new ArrayDeque<>(Arrays.asList(event.getFullCommandName().split(" ")));

        final SenderExtension<Sender, S> senderExtension = getCommandOptions().getCommandExtensions().getSenderExtension();
        final S sender = senderExtension.map(new SuggestionCommandSender(event));

        final LeafResult<Sender, S, Command.Choice> result = findExecutable(sender, getAppropriateMap(event), commands, true);
        if (result == null) return;

        final AutoCompleteQuery option = event.getFocusedOption();
        final List<String> arguments = event.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.toList());

        // On discord platforms, we don't need to navigate the command and can go straight into the argument.
        final InternalArgument<S, Command.Choice> argument = result.getCommand().getArgument(option.getName());
        if (argument == null) return;

        final List<Command.Choice> suggestions = argument.suggestions(sender, option.getValue(), arguments)
                .stream()
                .limit(25) // Discord only handles 25 at a time, :pensive:.
                .collect(Collectors.toList());

        event.replyChoices(suggestions).queue();
    }

    @Override
    public void registerCommand(final @NotNull Object command) {
        final RootCommandProcessor<Sender, S, Command.Choice> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final InternalRootCommand<Sender, S, Command.Choice> rootCommand = globalCommands
                .computeIfAbsent(name, it -> new InternalRootCommand<>(processor));

        rootCommand.addCommands(command, processor.commands(rootCommand));
    }

    public void registerCommand(final @NotNull Guild guild, final @NotNull List<@NotNull Object> commands) {
        for (final Object command : commands) {
            registerCommand(guild.getIdLong(), command);
        }
    }

    public void registerCommand(final @NotNull Guild guild, final @NotNull Object @NotNull ... commands) {
        for (final Object command : commands) {
            registerCommand(guild.getIdLong(), command);
        }
    }

    public void registerCommand(final @NotNull Guild guild, final @NotNull Object command) {
        registerCommand(guild.getIdLong(), command);
    }

    public void registerCommand(final @NotNull Long guildId, final @NotNull List<@NotNull Object> commands) {
        for (final Object command : commands) {
            registerCommand(guildId, command);
        }
    }

    public void registerCommand(final @NotNull Long guildId, final @NotNull Object @NotNull ... commands) {
        for (final Object command : commands) {
            registerCommand(guildId, command);
        }
    }

    public void registerCommand(final @NotNull Long guildId, final @NotNull Object command) {
        final RootCommandProcessor<Sender, S, Command.Choice> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final InternalRootCommand<Sender, S, Command.Choice> rootCommand = guildCommands
                .computeIfAbsent(guildId, it -> new HashMap<>())
                .computeIfAbsent(name, it -> new InternalRootCommand<>(processor));

        rootCommand.addCommands(command, processor.commands(rootCommand));
    }

    public void pushGuildCommands() {
        guildCommands.forEach((key, value) -> {
            final Guild guild = jda.getGuildById(key);
            if (guild == null) return;
            guild.updateCommands()
                    .addCommands(value.values().stream().map(JdaMappingUtil::mapCommand).collect(Collectors.toList()))
                    .queue();
        });
    }

    public void pushGlobalCommands() {
        jda.updateCommands()
                .addCommands(globalCommands.values().stream().map(JdaMappingUtil::mapCommand).collect(Collectors.toList()))
                .queue();
    }

    public void pushCommands() {
        pushGlobalCommands();
        pushGuildCommands();
    }

    @Override
    public void unregisterCommand(final @NotNull Object command) {

    }

    private @NotNull Map<String, InternalRootCommand<Sender, S, Command.Choice>> getAppropriateMap(final @NotNull CommandInteractionPayload event) {
        if (event.isGlobalCommand()) return globalCommands;

        final Guild guild = event.getGuild();
        if (guild == null) return Collections.emptyMap();
        return guildCommands.getOrDefault(guild.getIdLong(), Collections.emptyMap());
    }
}
