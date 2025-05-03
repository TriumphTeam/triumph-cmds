package dev.triumphteam.cmds.kord.sender

import dev.kord.core.entity.interaction.ChatInputCommandInteraction

public interface CommandSender : Sender {

    public val interation: ChatInputCommandInteraction
}
