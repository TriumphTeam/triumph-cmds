package dev.triumphteam.cmd.core.command;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ParentSubCommand<S> implements ParentCommand<S> {

    private final Map<String, Command<S>> commands = new HashMap<>();
    private final Map<String, Command<S>> commandAliases = new HashMap<>();

    @Override
    public void addSubCommand(
            final @NotNull String name,
            final @NotNull Command<S> subCommand,
            final boolean isAlias
    ) {

    }

    @Override
    public @NotNull Map<String, Command<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommandAliases() {
        return commandAliases;
    }
}
