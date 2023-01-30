package dev.triumphteam.slash.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.slash.sender.SlashCommandSender;
import net.dv8tion.jda.api.entities.User;

@Command("group")
public class ExampleCommandGroup {

    @Command("test")
    public class Group {

        @Command("first")
        public void first(final SlashCommandSender sender) {
            sender.reply("OH SHIT").queue();
        }

        @Command("second")
        public void second(final SlashCommandSender sender, final User user) {
            sender.reply("OH SHIT, OH FUCK " + user.getName() + " OH PISS!").queue();
        }
    }
}
