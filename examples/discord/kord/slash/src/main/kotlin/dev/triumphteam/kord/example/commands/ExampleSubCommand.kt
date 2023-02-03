package dev.triumphteam.kord.example.commands

import dev.kord.core.entity.User
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmds.kord.sender.SlashSender

@Command("sub")
public class ExampleSubCommand {
    @Command("first")
    public suspend fun first(sender: SlashSender) {
        sender.reply("Command sent was /sub first")
    }

    @Command("second")
    public suspend fun second(sender: SlashSender, user: User) {
        sender.reply("Command sent was /sub second <${user.username}>")
    }
}
