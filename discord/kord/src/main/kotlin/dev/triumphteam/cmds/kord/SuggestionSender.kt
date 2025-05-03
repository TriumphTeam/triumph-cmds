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

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.interaction.response.DeferredEphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.triumphteam.cmds.kord.sender.Sender

internal class SuggestionSender(event: AutoCompleteInteractionCreateEvent) : Sender {

    private val interaction = event.interaction

    override val guild: GuildBehavior?
        get() = (interaction as? GuildAutoCompleteInteraction)?.guild

    override val channel: ChannelBehavior = interaction.channel

    override val user: UserBehavior = interaction.user

    override suspend fun respondPublic(builder: InteractionResponseCreateBuilder.() -> Unit): PublicMessageInteractionResponseBehavior {
        error("Unsupported operation. Suggestions can't be responded to.")
    }

    override suspend fun respondEphemeral(builder: InteractionResponseCreateBuilder.() -> Unit): EphemeralMessageInteractionResponseBehavior {
        error("Unsupported operation. Suggestions can't be responded to.")
    }

    override suspend fun deferPublicResponse(): DeferredPublicMessageInteractionResponseBehavior {
        error("Unsupported operation. Suggestions can't be responded to.")
    }

    override suspend fun deferEphemeralResponse(): DeferredEphemeralMessageInteractionResponseBehavior {
        error("Unsupported operation. Suggestions can't be responded to.")
    }
}
