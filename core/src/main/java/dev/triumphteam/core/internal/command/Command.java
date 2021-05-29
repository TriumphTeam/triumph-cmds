package dev.triumphteam.core.internal.command;

import dev.triumphteam.core.internal.BaseCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command {

    @NotNull
    String getCommandName();

    @NotNull
    List<String> getAlias();

    boolean addSubCommands(@NotNull final BaseCommand baseCommand);

}
