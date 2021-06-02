package dev.triumphteam.cmds.bukkit;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.cmds.bukkit.factory.BukkitCommandFactory;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.CommandManager;
import dev.triumphteam.core.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class BukkitCommandManager extends CommandManager {

    private final Map<String, Command> commands = new HashMap<>();

    @Override
    public void registerCommand(final @NotNull BaseCommand command) {
        final BukkitCommand bukkitCommand = new BukkitCommandFactory(command).create();

        if (!bukkitCommand.addSubCommands(command)) {
            return;
        }

        commands.put(bukkitCommand.getName(), bukkitCommand);
    }


}
