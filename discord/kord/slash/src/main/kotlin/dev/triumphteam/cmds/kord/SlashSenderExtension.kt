package dev.triumphteam.cmds.kord

import dev.triumphteam.cmd.core.extention.sender.SenderExtension
import dev.triumphteam.cmds.kord.sender.SlashSender

internal class SlashSenderExtension : SenderExtension.Default<SlashSender> {

    override fun getAllowedSenders(): Set<Class<out SlashSender>> {
        return setOf(SlashSender::class.java)
    }
}
