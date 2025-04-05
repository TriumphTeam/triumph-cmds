package dev.triumphteam.bukkit.example;

import dev.triumphteam.bukkit.example.commands.ExampleCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this, builder -> {
        });

        commandManager.registerNamedArguments(
                ArgumentKey.of("query-parameters"),
                Argument.forBoolean().name("reversed").build(),
                Argument.forInt().name("r").build(),
                Argument.forString().name("since").build(),
                Argument.forString().name("before").build(),
                Argument.forString().name("cause").build(),
                Argument.forString().name("at").build(),
                Argument.forString().name("bounds").build(),
                Argument.forType(Material.class).name("ma").build(),
                Argument.listOf(String.class).name("a").build(),
                Argument.listOf(Material.class).name("m").build(),
                Argument.listOf(Player.class).name("p").build()
        );

        commandManager.registerCommand(new ExampleCommand());
    }
}
