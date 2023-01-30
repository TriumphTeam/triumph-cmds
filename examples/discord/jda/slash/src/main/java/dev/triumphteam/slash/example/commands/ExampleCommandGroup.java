package dev.triumphteam.slash.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.User;

@Command("group")
public class ExampleCommandGroup {

    @Command("test")
    public class Group {

        @Command("first")
        public void first(final SlashSender sender) {
            sender.reply("OH SHIT").queue();
        }

        @Command("second")
        public void second(final SlashSender sender, final User user) {
            sender.reply("OH SHIT, OH FUCK " + user.getName() + " OH PISS!").queue();
        }
    }
}
