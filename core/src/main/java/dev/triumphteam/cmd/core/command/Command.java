package dev.triumphteam.cmd.core.command;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Command<S> {

    private final String name;
    private final Map<String, SubCommand<S>> subCommands = new HashMap<>();

    public Command(final @NotNull String name) {
        this.name = name;
    }

    public void addSubCommands() {
        
    }
}
