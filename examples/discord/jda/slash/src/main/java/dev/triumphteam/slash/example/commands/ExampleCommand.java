package dev.triumphteam.slash.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.slash.sender.SlashCommandSender;
import net.dv8tion.jda.api.entities.User;

@Command("example")
public class ExampleCommand {

    @Command
    public void execute(final SlashCommandSender sender, @Suggestion("ass") final String name, final User user) {
        sender.reply("OH SHIT, " + name + " OH FUCK " + user.getName() + " OH PISS!").queue();
    }
}
