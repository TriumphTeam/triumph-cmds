package dev.triumphteam.slash.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.slash.annotation.Choice;
import dev.triumphteam.cmd.slash.annotation.NSFW;
import dev.triumphteam.cmd.slash.sender.SlashCommandSender;
import net.dv8tion.jda.api.entities.User;

@NSFW
@Command("example")
public class ExampleCommand {

    @Command
    public void execute(final SlashCommandSender sender, @Choice("hello") final String name, final User user) {
        sender.reply("Command sent was /example <" + name + "> <" + user.getName() + ">").queue();
    }
}
