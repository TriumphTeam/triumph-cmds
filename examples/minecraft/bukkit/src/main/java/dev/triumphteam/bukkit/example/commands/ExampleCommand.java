package dev.triumphteam.bukkit.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;

@Command("example")
public class ExampleCommand {

    @Command
    public void execute(final CommandSender sender, final String name) {
        sender.sendMessage("Example Command, name: " + name);
    }
}
