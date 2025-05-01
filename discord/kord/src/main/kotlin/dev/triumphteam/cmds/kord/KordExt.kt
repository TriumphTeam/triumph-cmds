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

import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
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
import dev.triumphteam.cmd.discord.ProvidedInternalArgument
import dev.triumphteam.cmd.discord.annotation.Choice

private val optionsMap: Map<Class<*>, (InternalArgument<*, *>) -> OptionsBuilder> = mapOf(
    Int::class.java to { argument ->
        IntegerOptionBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Short::class.java to { argument ->
        IntegerOptionBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Long::class.java to { argument ->
        NumberOptionBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Double::class.java to { argument ->
        NumberOptionBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Float::class.java to { argument ->
        NumberOptionBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Boolean::class.java to { argument ->
        BooleanBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Role::class.java to { argument ->
        RoleBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    User::class.java to { argument ->
        UserBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Member::class.java to { argument ->
        UserBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    TextChannel::class.java to { argument ->
        ChannelBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    MessageChannel::class.java to { argument ->
        ChannelBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    GuildChannel::class.java to { argument ->
        ChannelBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Attachment::class.java to { argument ->
        AttachmentBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
    Entity::class.java to { argument ->
        MentionableBuilder(argument.name, argument.desc).apply { defaults(argument) }
    },
)

private val InternalArgument<*, *>.desc
    get() = description.ifEmpty { name }

internal val InternalCommand<*, *>.desc
    get() = description.ifEmpty { name }

private fun OptionsBuilder.defaults(argument: InternalArgument<*, *>) {
    required = !argument.isOptional
    autocomplete = argument.shouldAutoComplete()
}

private fun InternalArgument<*, *>.shouldAutoComplete(): Boolean {
    if (meta.isPresent(Choice.META_KEY) || this is ProvidedInternalArgument) return false
    return canSuggest()
}

private fun InternalArgument<*, *>.toKordOption(): OptionsBuilder =
    optionsMap[type]?.invoke(this) ?: StringChoiceBuilder(
        name,
        desc
    ).apply { defaults(this@toKordOption) }

internal fun InternalLeafCommand<*, *>.mapArgumentsToKord(): MutableList<OptionsBuilder> =
    argumentList.map { arg -> arg.toKordOption() }.toMutableList()
