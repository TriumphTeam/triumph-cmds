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
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import dev.triumphteam.cmd.discord.LeafResult;
import dev.triumphteam.cmd.discord.ProvidedInternalArgument;
import dev.triumphteam.cmd.discord.choices.ChoiceKey;
import dev.triumphteam.cmd.jda.sender.SlashSender;
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
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.triumphteam.cmd.discord.CommandWalkUtil.findExecutable;

/**
 * Command Manager for Slash Commands.
 * Allows for registering of global and guild-specific commands.
 * As well as the implementation of custom command senders.
 *
 * @param <S> The sender type.
 */
public final class SlashCommandManager<S> extends CommandManager<SlashSender, S, SlashCommandOptions<S>> {

    private final JDA jda;

    private final SlashRegistryContainer<S> registryContainer;

    private final Map<String, InternalRootCommand<SlashSender, S>> globalCommands = new HashMap<>();
    private final Map<Long, Map<String, InternalRootCommand<SlashSender, S>>> guildCommands = new HashMap<>();

    private SlashCommandManager(
            final @NotNull JDA jda,
            final @NotNull SlashCommandOptions<S> commandOptions,
            final @NotNull SlashRegistryContainer<S> registryContainer
    ) {
        super(commandOptions);
        this.jda = jda;
        this.registryContainer = registryContainer;

        // All use the same factory
        final InternalArgument.Factory<S> jdaArgumentFactory = ProvidedInternalArgument::new;

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
                jda.addEventListener(new SlashCommandsListener(this));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Contract("_, _, _ -> new")
    public static <S> @NotNull SlashCommandManager<S> create(
            final @NotNull JDA jda,
            final @NotNull SenderExtension<SlashSender, S> senderExtension,
            final @NotNull Consumer<SlashCommandOptions.Builder<S>> builder
    ) {
        final SlashRegistryContainer<S> registryContainer = new SlashRegistryContainer<>();
        final SlashCommandOptions.Builder<S> extensionBuilder = new SlashCommandOptions.Builder<>(registryContainer);
        builder.accept(extensionBuilder);
        return new SlashCommandManager<>(jda, extensionBuilder.build(senderExtension), registryContainer);
    }

    @Contract("_, _ -> new")
    public static @NotNull SlashCommandManager<SlashSender> create(
            final @NotNull JDA jda,
            final @NotNull Consumer<SlashCommandOptions.Builder<SlashSender>> builder
    ) {
        final SlashRegistryContainer<SlashSender> registryContainer = new SlashRegistryContainer<>();
        final SlashCommandOptions.Builder<SlashSender> extensionBuilder = new SlashCommandOptions.Builder<>(registryContainer);

        // Setup defaults for Bukkit
        final MessageRegistry<SlashSender> messageRegistry = registryContainer.getMessageRegistry();
        setUpDefaults(messageRegistry);

        // Then accept configured values
        builder.accept(extensionBuilder);
        return new SlashCommandManager<>(jda, extensionBuilder.build(new SlashSenderExtension()), registryContainer);
    }

    @Contract("_ -> new")
    public static @NotNull SlashCommandManager<SlashSender> create(final @NotNull JDA jda) {
        return create(jda, builder -> {});
    }

    private static void setUpDefaults(final @NotNull MessageRegistry<SlashSender> registry) {
        registry.register(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.reply("Unknown command: `" + context.getInvalidInput() + "`.").setEphemeral(true).queue());
        registry.register(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        registry.register(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.reply("Invalid usage.").setEphemeral(true).queue());
        registry.register(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.reply("Invalid argument `" + context.getInvalidInput() + "` for type `" + context.getArgumentType().getSimpleName() + "`.").setEphemeral(true).queue());
    }

    public void registerChoices(final @NotNull ChoiceKey key, final @NotNull Supplier<List<String>> choiceSupplier) {
        registryContainer.getChoiceRegistry().register(key, choiceSupplier);
    }

    public void execute(final @NotNull SlashCommandInteractionEvent event) {
        final Deque<String> commands = new ArrayDeque<>(Arrays.asList(event.getFullCommandName().split(" ")));

        final SenderExtension<SlashSender, S> senderExtension = getCommandOptions().getCommandExtensions().getSenderExtension();
        final S sender = senderExtension.map(new InteractionCommandSender(event));

        final LeafResult<SlashSender, S> result = findExecutable(sender, getAppropriateMap(event), commands, true);
        if (result == null) return;

        // Mapping of arguments
        final Map<String, ArgumentInput> arguments = new HashMap<>();
        for (final OptionMapping option : event.getOptions()) {
            arguments.put(option.getName(), JdaMappingUtil.parsedValueFromType(option));
        }

        try {
            result.getCommand().execute(sender, result.getInstanceSupplier(), arguments);
        } catch (final @NotNull Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
    }

    public void suggest(final @NotNull CommandAutoCompleteInteractionEvent event) {
        final Deque<String> commands = new ArrayDeque<>(Arrays.asList(event.getFullCommandName().split(" ")));

        final SenderExtension<SlashSender, S> senderExtension = getCommandOptions().getCommandExtensions().getSenderExtension();
        final S sender = senderExtension.map(new SuggestionCommandSender(event));

        final LeafResult<SlashSender, S> result = findExecutable(sender, getAppropriateMap(event), commands, true);
        if (result == null) return;

        final AutoCompleteQuery option = event.getFocusedOption();
        final List<String> arguments = event.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.toList());

        // On discord platforms, we don't need to navigate the command and can go straight into the argument.
        final InternalArgument<S> argument = result.getCommand().getArgument(option.getName());
        if (argument == null) return;

        final List<String> suggestions = argument.suggestions(sender, option.getValue(), arguments)
                .stream()
                .limit(25) // Discord only handles 25 at a time, :pensive:.
                .collect(Collectors.toList());

        event.replyChoices(JdaMappingUtil.mapChoices(suggestions, JdaMappingUtil.fromType(argument.getType()))).queue();
    }

    @Override
    public void registerCommand(final @NotNull Object command) {
        final RootCommandProcessor<SlashSender, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final InternalRootCommand<SlashSender, S> rootCommand = globalCommands
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
        final RootCommandProcessor<SlashSender, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final InternalRootCommand<SlashSender, S> rootCommand = guildCommands
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

    @Override
    protected @NotNull SlashRegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    private @NotNull Map<String, InternalRootCommand<SlashSender, S>> getAppropriateMap(final @NotNull CommandInteractionPayload event) {
        if (event.isGlobalCommand()) return globalCommands;

        final Guild guild = event.getGuild();
        if (guild == null) return Collections.emptyMap();
        return guildCommands.getOrDefault(guild.getIdLong(), Collections.emptyMap());
    }

    private @Nullable InternalRootCommand<SlashSender, S> getCommand(
            final @NotNull CommandInteractionPayload event,
            final @NotNull String name
    ) {
        if (event.isGlobalCommand()) return globalCommands.get(name);

        final Guild guild = event.getGuild();
        // Should never be null here
        if (guild == null) return null;
        return guildCommands
                .getOrDefault(guild.getIdLong(), Collections.emptyMap())
                .get(name);
    }
}
