package dev.triumphteam.slash.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.slash.sender.SlashCommandSender;
import net.dv8tion.jda.api.entities.User;

@Command("sub")
public class ExampleSubCommand {

    @Command("first")
    public void first(final SlashCommandSender sender) {
        sender.reply("OH PISS!").queue();
    }

    @Command("second")
    public void second(final SlashCommandSender sender, final User user) {
        sender.reply("OH SHIT, " + user.getName() + " OH PISS!").queue();
    }
}
