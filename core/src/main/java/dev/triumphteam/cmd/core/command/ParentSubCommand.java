package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.processor.ParentCommandProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ParentSubCommand<S> implements ParentCommand<S> {

    private final Map<String, Command<S>> commands = new HashMap<>();
    private final Map<String, Command<S>> commandAliases = new HashMap<>();

    private final String name;
    private final CommandMeta meta;

    public ParentSubCommand(
            final @NotNull ParentCommandProcessor<S> processor
    ) {
        this.name = processor.getName();
        this.meta = processor.createMeta();
    }

    @Override
    public void addSubCommand(final @NotNull Command<S> subCommand, final boolean isAlias) {
        commands.put(subCommand.getName(), subCommand);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
