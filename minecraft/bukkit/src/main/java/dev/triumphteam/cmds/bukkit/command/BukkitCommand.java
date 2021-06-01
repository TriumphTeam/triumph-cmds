package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitCommand implements Command {

    @Override
    public @NotNull String getName() {
        return null;
    }

    @Override
    public @NotNull List<String> getAlias() {
        return null;
    }

    @Override
    public boolean addSubCommands(final @NotNull BaseCommand baseCommand) {
        return false;
    }

    @Override
    public boolean isAlias() {
        return false;
    }

}
