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
package dev.triumphteam.cmds.kord

import dev.kord.common.entity.Choice
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.suggest
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.ResolvableOptionValue
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.subCommand
import dev.triumphteam.cmd.core.CommandManager
import dev.triumphteam.cmd.core.argument.InternalArgument
import dev.triumphteam.cmd.core.command.ArgumentInput
import dev.triumphteam.cmd.core.command.InternalBranchCommand
import dev.triumphteam.cmd.core.command.InternalCommand
import dev.triumphteam.cmd.core.command.InternalLeafCommand
import dev.triumphteam.cmd.core.command.InternalRootCommand
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer
import dev.triumphteam.cmd.core.extension.sender.SenderExtension
import dev.triumphteam.cmd.core.processor.RootCommandProcessor
import dev.triumphteam.cmd.discord.CommandWalkUtil.findExecutable
import dev.triumphteam.cmd.discord.ProvidedInternalArgument
import dev.triumphteam.cmd.discord.annotation.NSFW
import dev.triumphteam.cmds.contains
import dev.triumphteam.cmds.kord.sender.Sender
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

public fun KordCommandManager(
    kord: Kord,
    builder: KordCommandOptions.Builder<Sender>.() -> Unit = {},
): KordCommandManager<Sender> = KordCommandManager(kord, SlashSenderExtension(), builder)

public fun <S> KordCommandManager(
    kord: Kord,
    senderExtension: SenderExtension<Sender, S>,
    builder: KordCommandOptions.Builder<S>.() -> Unit = {},
): KordCommandManager<S> {
    val registryContainer = RegistryContainer<Sender, S, Choice>()
    return KordCommandManager(
        kord = kord,
        commandOptions = KordCommandOptions.Builder<S>(kord).apply(builder).build(senderExtension),
        registryContainer = registryContainer,
    )
}

public class KordCommandManager<S> internal constructor(
    private val kord: Kord,
    commandOptions: KordCommandOptions<S>,
    private val registryContainer: RegistryContainer<Sender, S, Choice>,
) : CommandManager<Sender, S, KordCommandOptions<S>, Choice>(commandOptions, registryContainer) {

    private val globalCommands: MutableMap<String, InternalRootCommand<Sender, S, Choice>> = mutableMapOf()
    private val guildCommands: MutableMap<Snowflake, MutableMap<String, InternalRootCommand<Sender, S, Choice>>> =
        mutableMapOf()

    private val commandQueue: MutableList<suspend () -> Unit> = mutableListOf()

    private var isKordReady = false

    init {
        kord.on(consumer = ::execute)
        kord.on(consumer = ::suggest)
        kord.on<ReadyEvent> {
            // Sets kord to ready
            isKordReady = true
            commandQueue.forEach { it() }
        }

        // All use the same factory
        val providedArgumentFactory =
            InternalArgument.Factory<S, Choice> { meta, name, description, type, suggestion, optional ->
                ProvidedInternalArgument(meta, name, description, type, suggestion, optional)
            }

        registerArgument(User::class.java, providedArgumentFactory)
        registerArgument(Role::class.java, providedArgumentFactory)
        registerArgument(Member::class.java, providedArgumentFactory)
        registerArgument(GuildChannel::class.java, providedArgumentFactory)
        registerArgument(TextChannel::class.java, providedArgumentFactory)
        registerArgument(MessageChannel::class.java, providedArgumentFactory)
        registerArgument(Attachment::class.java, providedArgumentFactory)
        registerArgument(Entity::class.java, providedArgumentFactory)
    }

    override fun registerCommand(command: Any) {
        TODO("Not yet implemented")
    }

    public fun registerCommand(guild: Guild, commands: List<Any>): Unit = commands.forEach {
        registerCommand(guild, it)
    }

    public fun registerCommand(guild: Guild, vararg commands: Any): Unit = commands.forEach {
        registerCommand(guild, it)
    }

    public fun registerCommand(guild: Guild, command: Any) {
        registerCommand(guild.id, command)
    }

    public fun registerCommand(guildId: Snowflake, command: Any) {
        val processor: RootCommandProcessor<Sender, S, Choice> = RootCommandProcessor(
            command,
            registryContainer,
            commandOptions
        )

        val name = processor.name

        // Get or add a command, then add its sub commands
        val rootCommand: InternalRootCommand<Sender, S, Choice> = guildCommands
            .getOrPut(guildId) { mutableMapOf() }
            .getOrPut(name) { InternalRootCommand(processor) }

        rootCommand.addCommands(command, processor.commands(rootCommand))

        if (!isKordReady) {
            commandQueue.add { registerKordCommand(guildId, rootCommand) }
            return
        }

        kord.launch {
            registerKordCommand(guildId, rootCommand)
        }
    }

    override fun unregisterCommand(command: Any) {
        TODO("Not yet implemented")
    }

    private fun execute(event: ChatInputCommandInteractionCreateEvent) {
        val commands = event.interaction.command.fullName

        val senderExtension: SenderExtension<Sender, S> = commandOptions.commandExtensions.senderExtension
        val sender = senderExtension.map(ChatInputSender(event))

        val result = findExecutable(sender, getAppropriateMap(event.guildId), commands, true)
        if (result == null) return

        val arguments = event.interaction.command.options.mapNotNull { (key, value) ->
            when (value) {
                is ResolvableOptionValue<*> -> {
                    val resolvedObject = value.resolvedObject ?: return@mapNotNull null
                    key to ArgumentInput(resolvedObject.toString(), resolvedObject)
                }

                else -> {
                    val wrappedValue = value.value.toString()
                    key to ArgumentInput(wrappedValue, wrappedValue)
                }
            }
        }.toMap()

        runCatching {
            result.getCommand().execute(sender, result.instanceSupplier, arguments)
        }.onFailure { throwable ->
            throw CommandExecutionException("An error occurred while executing the command")
                .initCause(if (throwable is InvocationTargetException) throwable.cause else throwable)
        }
    }

    private suspend fun suggest(event: AutoCompleteInteractionCreateEvent) {
        val commands = event.interaction.command.fullName

        val senderExtension: SenderExtension<Sender, S> = commandOptions.commandExtensions.senderExtension
        val sender = senderExtension.map(SuggestionSender(event))

        val result = findExecutable(sender, getAppropriateMap(event.guildId), commands, true)
        if (result == null) return

        val options = event.interaction.command.options
            .map { (key, value) -> KordOption(key, value.value.toString(), value.focused) }

        val focused = options.find(KordOption::focused) ?: return
        val arguments = options.map(KordOption::value)

        // On discord platforms, we don't need to navigate the command and can go straight into the argument.
        val argument = result.getCommand().getArgument(focused.name)
        if (argument == null) return

        val suggestions = argument.suggestions(sender, focused.value, arguments).take(25)
        event.interaction.suggest(suggestions)
    }

    private suspend fun registerKordCommand(
        guildId: Snowflake,
        rootCommand: InternalRootCommand<Sender, S, Choice>,
    ) {
        kord.createGuildChatInputCommand(
            guildId,
            rootCommand.name,
            rootCommand.kordDescription
        ) {

            nsfw = NSFW.META_KEY in rootCommand.meta

            // Handle the default command first.
            rootCommand.getCommand(InternalCommand.DEFAULT_CMD_NAME)?.let { command ->
                if (command !is InternalLeafCommand) return@let
                options = command.kordArguments
                return@createGuildChatInputCommand
            }

            val commands = rootCommand.commands.values.filterNot { it.isHidden }

            // Handle normal sub commands.
            commands.filterIsInstance<InternalLeafCommand<Sender, S, Choice>>().forEach {
                subCommand(it.name, it.kordDescription) {
                    options = it.kordArguments
                }
            }

            // Handle group sub commands.
            commands.filterIsInstance<InternalBranchCommand<Sender, S, Choice>>().forEach {
                group(it.name, it.kordDescription) {
                    it.commands.values.filterIsInstance<InternalLeafCommand<Sender, S, Choice>>().forEach { sub ->
                        subCommand(sub.name, sub.kordDescription) {
                            options = sub.kordArguments
                        }
                    }
                }
            }
        }
    }

    private val ChatInputCommandInteractionCreateEvent.guildId: Snowflake?
        get() = (interaction as? GuildChatInputCommandInteraction)?.guildId

    private val AutoCompleteInteractionCreateEvent.guildId: Snowflake?
        get() = (interaction as? GuildAutoCompleteInteraction)?.guildId

    private fun getAppropriateMap(guildId: Snowflake?): Map<String, InternalRootCommand<Sender, S, Choice>> {
        if (guildId != null) {
            return guildCommands[guildId] ?: mutableMapOf()
        }

        return globalCommands
    }
}
