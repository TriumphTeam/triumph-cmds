package dev.triumphteam.cmd.prefixed.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PrefixedCommand implements Command {

    private final String name;
    private final List<String> alias;

    public PrefixedCommand(@NotNull final PrefixedCommandProcessor prefixedCommandFactory) {
        this.name = prefixedCommandFactory.getName();
        this.alias = prefixedCommandFactory.getAlias();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public List<String> getAlias() {
        return alias;
    }

    @Override
    public boolean addSubCommands(@NotNull final BaseCommand baseCommand) {
        return true;
    }

}
