package dev.triumphteam.bukkit.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.CommandFlags;
import dev.triumphteam.cmd.core.annotations.NamedArguments;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import org.bukkit.command.CommandSender;

@Command("example")
public class ExampleCommand {

    @Command
    @NamedArguments("query-parameters")
    @CommandFlags(key = "test.flags")
    public void execute(final CommandSender sender, final String name, final Arguments arguments) {
        sender.sendMessage("Example Command, name: " + name);
        System.out.println(arguments);
    }
}
