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
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.ResolvableOptionValue
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildAutoCompleteInteractionCreateEvent
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
import dev.triumphteam.cmds.kord.sender.SlashSender
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

public fun SlashCommandManager(
    kord: Kord,
    builder: SlashCommandOptions.Builder<SlashSender>.() -> Unit = {},
): SlashCommandManager<SlashSender> = SlashCommandManager(kord, SlashSenderExtension(), builder)

public fun <S> SlashCommandManager(
    kord: Kord,
    senderExtension: SenderExtension<SlashSender, S>,
    builder: SlashCommandOptions.Builder<S>.() -> Unit = {},
): SlashCommandManager<S> {
    val registryContainer = RegistryContainer<SlashSender, S, Choice>()
    return SlashCommandManager(
        kord = kord,
        commandOptions = SlashCommandOptions.Builder<S>(kord).apply(builder).build(senderExtension),
        registryContainer = registryContainer,
    )
}

public class SlashCommandManager<S> internal constructor(
    private val kord: Kord,
    commandOptions: SlashCommandOptions<S>,
    private val registryContainer: RegistryContainer<SlashSender, S, Choice>,
) : CommandManager<SlashSender, S, SlashCommandOptions<S>, Choice>(commandOptions, registryContainer) {

    private val globalCommands: MutableMap<String, InternalRootCommand<SlashSender, S, Choice>> = mutableMapOf()
    private val guildCommands: MutableMap<Snowflake, MutableMap<String, InternalRootCommand<SlashSender, S, Choice>>> =
        mutableMapOf()

    private val commandQueue: MutableList<suspend () -> Unit> = mutableListOf()

    private var isKordReady = false

    init {
        kord.on(consumer = ::execute)
        kord.on(consumer = ::suggest)
        kord.on<ReadyEvent> {
            // Sets kord to ready
            isKordReady = true
            commandQueue.forEach { it.invoke() }
        }

        // All use the same factory
        val jdaArgumentFactory =
            InternalArgument.Factory<S, Choice> { meta, name, description, type, suggestion, optional ->
                ProvidedInternalArgument(meta, name, description, type, suggestion, optional)
            }

        registerArgument(User::class.java, jdaArgumentFactory)
        registerArgument(Role::class.java, jdaArgumentFactory)
        registerArgument(Member::class.java, jdaArgumentFactory)
        registerArgument(GuildChannel::class.java, jdaArgumentFactory)
        registerArgument(TextChannel::class.java, jdaArgumentFactory)
        registerArgument(MessageChannel::class.java, jdaArgumentFactory)
        registerArgument(Attachment::class.java, jdaArgumentFactory)
        registerArgument(Entity::class.java, jdaArgumentFactory)
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
        val processor: RootCommandProcessor<SlashSender, S, Choice> = RootCommandProcessor(
            command,
            registryContainer,
            commandOptions
        )

        val name = processor.name

        // Get or add a command, then add its sub commands
        val rootCommand: InternalRootCommand<SlashSender, S, Choice> = guildCommands
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
        val commands = event.interaction.fullNAME

        val senderExtension: SenderExtension<SlashSender, S> = commandOptions.commandExtensions.senderExtension
        val sender = senderExtension.map(DummySender(event))

        val result = findExecutable(sender, getAppropriateMap(event), commands, true)
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

    private suspend fun suggest(event: GuildAutoCompleteInteractionCreateEvent) {
        event.interaction.suggestString {
            choice("hello", "test")
        }
    }

    private suspend fun registerKordCommand(
        guildId: Snowflake,
        rootCommand: InternalRootCommand<SlashSender, S, Choice>,
    ) {
        kord.createGuildChatInputCommand(
            guildId,
            rootCommand.name,
            rootCommand.kordDescription
        ) {

            // Handle the default command first.
            rootCommand.getCommand(InternalCommand.DEFAULT_CMD_NAME)?.let { command ->
                if (command !is InternalLeafCommand) return@let
                options = command.kordArguments
                return@createGuildChatInputCommand
            }

            val commands = rootCommand.commands.values.filterNot { it.isHidden }

            // Handle normal sub commands.
            commands.filterIsInstance<InternalLeafCommand<SlashSender, S, Choice>>().forEach {
                subCommand(it.name, it.kordDescription) {
                    options = it.kordArguments
                }
            }

            // Handle group sub commands.
            commands.filterIsInstance<InternalBranchCommand<SlashSender, S, Choice>>().forEach {
                group(it.name, it.kordDescription) {
                    it.commands.values.filterIsInstance<InternalLeafCommand<SlashSender, S, Choice>>().forEach { sub ->
                        subCommand(sub.name, sub.kordDescription) {
                            options = sub.kordArguments
                        }
                    }
                }
            }
        }
    }

    private fun getAppropriateMap(event: ChatInputCommandInteractionCreateEvent): Map<String, InternalRootCommand<SlashSender, S, Choice>> {
        val interaction = event.interaction
        if (interaction is GuildChatInputCommandInteraction) {
            return guildCommands[interaction.guildId] ?: mutableMapOf()
        }

        return globalCommands
    }
}
