package dev.triumphteam.bukkit.example;

import dev.triumphteam.bukkit.example.commands.ExampleCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this, builder -> {

        });
        commandManager.registerCommand(new ExampleCommand());
    }
}
