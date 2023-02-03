package dev.triumphteam.cmds.kord

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.triumphteam.cmds.kord.sender.SlashSender

internal class DummySender(private val event: ChatInputCommandInteractionCreateEvent) : SlashSender {
    override suspend fun reply(message: String) {
        event.interaction.respondPublic {
            content = message
        }
    }
}
