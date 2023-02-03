package dev.triumphteam.kord.example.commands

import dev.kord.core.entity.User
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmds.kord.sender.SlashSender

@Command("group")
public class ExampleCommandGroup {
    @Command("test")
    public inner class Group {
        @Command("first")
        public suspend fun first(sender: SlashSender) {
            sender.reply("Command sent was /group test first")
        }

        @Command("second")
        public suspend fun second(sender: SlashSender, user: User) {
            sender.reply("Command sent was /group test second <$user>")
        }
    }
}
