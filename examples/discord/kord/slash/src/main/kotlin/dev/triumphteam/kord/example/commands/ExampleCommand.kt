package dev.triumphteam.kord.example.commands

import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.discord.annotation.NSFW
import dev.triumphteam.cmds.kord.sender.SlashSender

@NSFW
@Command("example")
public class ExampleCommand {
    @Command
    public suspend fun execute(sender: SlashSender, member: Member, user: User, attachment: Attachment) {
        sender.reply("Command sent was /example <${member.username}> <${user.username}> <${attachment.filename}>")
    }
}
