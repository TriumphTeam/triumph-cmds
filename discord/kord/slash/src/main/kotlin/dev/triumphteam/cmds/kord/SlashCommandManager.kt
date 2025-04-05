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
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.ResolvableOptionValue
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildAutoCompleteInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.subCommand
import dev.triumphteam.cmd.core.CommandManager
import dev.triumphteam.cmd.core.argument.InternalArgument
import dev.triumphteam.cmd.core.command.ParentSubCommand
import dev.triumphteam.cmd.core.command.RootCommand
import dev.triumphteam.cmd.core.command.SubCommand
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer
import dev.triumphteam.cmd.core.extension.sender.SenderExtension
import dev.triumphteam.cmd.core.processor.RootCommandProcessor
import dev.triumphteam.cmd.core.util.Pair
import dev.triumphteam.cmd.discord.ProvidedInternalArgument
import dev.triumphteam.cmds.kord.sender.SlashSender
import kotlinx.coroutines.launch
import java.util.ArrayDeque

public fun SlashCommandManager(
    kord: Kord,
    builder: SlashCommandOptions.Builder<SlashSender>.() -> Unit = {},
): SlashCommandManager<SlashSender> = SlashCommandManager(kord, SlashSenderExtension(), builder)

public fun <S> SlashCommandManager(
    kord: Kord,
    senderExtension: SenderExtension<SlashSender, S>,
    builder: SlashCommandOptions.Builder<S>.() -> Unit = {},
): SlashCommandManager<S> {
    val registryContainer = SlashRegistryContainer<S>()
    return SlashCommandManager(
        kord,
        SlashCommandOptions
            .Builder(registryContainer, kord)
            .apply(builder)
            .build(senderExtension),
        registryContainer
    )
}

public class SlashCommandManager<S>(
    private val kord: Kord,
    commandOptions: SlashCommandOptions<S>,
    private val registryContainer: SlashRegistryContainer<S>,
) : CommandManager<SlashSender, S, SlashCommandOptions<S>>(commandOptions) {

    private val globalCommands: MutableMap<String, RootCommand<SlashSender, S>> = mutableMapOf()
    private val guildCommands: MutableMap<Snowflake, MutableMap<String, RootCommand<SlashSender, S>>> = mutableMapOf()

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
        val jdaArgumentFactory = InternalArgument.Factory<S> { meta, name, description, type, suggestion, optional ->
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
        val processor: RootCommandProcessor<SlashSender, S> = RootCommandProcessor(
            command,
            registryContainer,
            commandOptions
        )

        val name = processor.name

        // Get or add command, then add its sub commands
        val rootCommand: RootCommand<SlashSender, S> = guildCommands
            .getOrPut(guildId) { mutableMapOf() }
            .getOrPut(name) { RootCommand(processor) }

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

    override fun getRegistryContainer(): RegistryContainer<SlashSender, S> {
        return registryContainer
    }

    private suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
        val command = event.interaction.command

        val name = command.rootName

        val commands = ArrayDeque<String>()
        if (command is dev.kord.core.entity.interaction.SubCommand) {
            commands.add(command.name)
        }

        if (command is GroupCommand) {
            commands.add(command.groupName)
            commands.add(command.name)
        }

        val rootCommand = guildCommands[event.interaction.invokedCommandGuildId]?.get(name) ?: return

        val sender = commandOptions.commandExtensions.senderExtension.map(DummySender(event))

        // Mapping all arguments
        val arguments: Map<String, Pair<String, Any>> = command.options.map { (key, value) ->
            when (value) {
                is ResolvableOptionValue<*> -> {
                    val resolvedObject = value.resolvedObject
                    key to Pair(resolvedObject.toString(), resolvedObject as Any)
                }
                else -> {
                    val wrappedValue = value.value
                    key to Pair(wrappedValue.toString(), wrappedValue.toString() as Any)
                }
            }
        }.toMap()

        rootCommand.executeNonLinear(sender, null, commands, arguments)
    }

    private suspend fun suggest(event: GuildAutoCompleteInteractionCreateEvent) {
        event.interaction.suggestString {
            choice("hello", "test")
        }
    }

    private suspend fun registerKordCommand(guildId: Snowflake, rootCommand: RootCommand<SlashSender, S>) {
        kord.createGuildChatInputCommand(
            guildId,
            rootCommand.name,
            rootCommand.desc
        ) {
            // If only default then register with no groups or sub commands
            rootCommand.defaultCommand?.let {
                if (it is SubCommand<SlashSender, S>) {
                    options = it.mapArgumentsToKord()
                }
                return@createGuildChatInputCommand
            }

            val commands = rootCommand.commands.values

            commands.filterIsInstance<SubCommand<SlashSender, S>>().forEach {
                subCommand(it.name, it.desc) {
                    options = it.mapArgumentsToKord()
                }
            }

            commands.filterIsInstance<ParentSubCommand<SlashSender, S>>().forEach {
                group(it.name, it.desc) {
                    it.commands.values.filterIsInstance<SubCommand<SlashSender, S>>().forEach { sub ->
                        subCommand(sub.name, sub.desc) {
                            options = sub.mapArgumentsToKord()
                        }
                    }
                }
            }
        }
    }
}
