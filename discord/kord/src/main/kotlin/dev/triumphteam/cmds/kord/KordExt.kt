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

import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.AttachmentBuilder
import dev.kord.rest.builder.interaction.BooleanBuilder
import dev.kord.rest.builder.interaction.ChannelBuilder
import dev.kord.rest.builder.interaction.IntegerOptionBuilder
import dev.kord.rest.builder.interaction.MentionableBuilder
import dev.kord.rest.builder.interaction.NumberOptionBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.RoleBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import dev.kord.rest.builder.interaction.UserBuilder
import dev.triumphteam.cmd.core.argument.InternalArgument
import dev.triumphteam.cmd.core.command.InternalCommand
import dev.triumphteam.cmd.core.command.InternalLeafCommand
import java.util.ArrayDeque
import java.util.Deque

private val TYPE_MAPPING: Map<Class<*>, ApplicationCommandOptionType> = mapOf(
    Int::class.java to ApplicationCommandOptionType.Integer,
    Short::class.java to ApplicationCommandOptionType.Integer,
    Long::class.java to ApplicationCommandOptionType.Integer,
    Double::class.java to ApplicationCommandOptionType.Number,
    Float::class.java to ApplicationCommandOptionType.Number,
    Boolean::class.java to ApplicationCommandOptionType.Boolean,
    Role::class.java to ApplicationCommandOptionType.Role,
    User::class.java to ApplicationCommandOptionType.User,
    Member::class.java to ApplicationCommandOptionType.User,
    TextChannel::class.java to ApplicationCommandOptionType.Channel,
    MessageChannel::class.java to ApplicationCommandOptionType.Channel,
    GuildChannel::class.java to ApplicationCommandOptionType.Channel,
    Attachment::class.java to ApplicationCommandOptionType.Attachment,
    Entity::class.java to ApplicationCommandOptionType.Mentionable,
)

private val OPTION_BUILDER_MAPPING: Map<ApplicationCommandOptionType, (InternalArgument<*, *>) -> OptionsBuilder> =
    mapOf(
        ApplicationCommandOptionType.Integer to { argument ->
            IntegerOptionBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Number to { argument ->
            NumberOptionBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Boolean to { argument ->
            BooleanBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Role to { argument ->
            RoleBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.User to { argument ->
            UserBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Channel to { argument ->
            ChannelBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Attachment to { argument ->
            AttachmentBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
        ApplicationCommandOptionType.Mentionable to { argument ->
            MentionableBuilder(argument.name, argument.kordDescription).apply { defaults(argument) }
        },
    )

internal val InternalArgument<*, *>.kordType: ApplicationCommandOptionType
    get() = type.kordType

internal val Class<*>.kordType: ApplicationCommandOptionType
    get() = TYPE_MAPPING[this] ?: ApplicationCommandOptionType.String

private val InternalArgument<*, *>.kordDescription
    get() = description.ifEmpty { name }

internal val InternalCommand<*, *, *>.kordDescription
    get() = description.ifEmpty { name }

internal val ChatInputCommandInteraction.fullNAME: Deque<String>
    get() {
        val command = command // Needed for smart casting.
        return ArrayDeque(
            buildList {
                add(command.rootName) // Root is always there.

                if (command is GroupCommand) {
                    add(command.groupName)
                    add(command.name)
                    return@buildList
                }

                if (command is SubCommand) {
                    add(command.name)
                }
            }
        )
    }

private fun OptionsBuilder.defaults(argument: InternalArgument<*, *>) {
    required = !argument.isOptional
    // autocomplete = argument.shouldAutoComplete()
}

private fun InternalArgument<*, *>.toKordOption(): OptionsBuilder =
    OPTION_BUILDER_MAPPING[kordType]?.invoke(this) ?: StringChoiceBuilder(
        name,
        kordDescription
    ).apply { defaults(this@toKordOption) }

internal fun InternalLeafCommand<*, *, *>.mapArgumentsToKord(): MutableList<OptionsBuilder> =
    argumentList.map { arg -> arg.toKordOption() }.toMutableList()

