package dev.triumphteam.cmd.prefixed.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PrefixedCommand implements Command {

    private final String prefix;
    private final String name;
    private final List<String> alias;

    public PrefixedCommand(@NotNull final PrefixedCommandFactory prefixedCommandFactory) {
        this.prefix = prefixedCommandFactory.getPrefix();
        this.name = prefixedCommandFactory.getName();
        this.alias = prefixedCommandFactory.getAlias();
    }

    @NotNull
    public String getPrefix() {
        return "";
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
        return false;
    }

}
