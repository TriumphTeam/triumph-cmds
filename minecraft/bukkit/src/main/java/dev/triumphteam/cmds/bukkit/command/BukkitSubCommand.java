package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.core.command.SubCommand;

public final class BukkitSubCommand implements SubCommand {

    private final String name;

    public BukkitSubCommand(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
    
}
