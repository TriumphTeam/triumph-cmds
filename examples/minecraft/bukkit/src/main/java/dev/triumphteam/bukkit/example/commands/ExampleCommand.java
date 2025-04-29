package dev.triumphteam.bukkit.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.CommandFlags;
import dev.triumphteam.cmd.core.annotations.NamedArguments;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command("example")
public class ExampleCommand {

    @Command
    @NamedArguments("query-parameters")
    @CommandFlags(key = "test.flags")
    public void execute(final CommandSender sender, final String name, final Arguments arguments) {
        sender.sendMessage("Example Command, name: " + name);
        arguments.getArgument("ma", Material.class).ifPresent(ma -> sender.sendMessage("ma: " + ma));
        final List<Material> hmm = arguments.getListArgument("m", Material.class).get();
        hmm.forEach((material) -> {
            sender.sendMessage("Material: " + material.toString());
        });
    }
}
