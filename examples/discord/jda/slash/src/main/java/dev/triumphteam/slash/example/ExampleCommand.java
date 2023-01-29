package dev.triumphteam.slash.example;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.User;

@Command("example")
public class ExampleCommand {

    @Command
    public void execute(final SlashSender sender, final String name, final User user) {

    }
}
