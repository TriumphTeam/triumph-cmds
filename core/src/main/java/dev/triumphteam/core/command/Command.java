package dev.triumphteam.core.command;

import dev.triumphteam.core.BaseCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command {

    @NotNull
    String getName();

    @NotNull
    List<String> getAlias();

    boolean addSubCommands(@NotNull final BaseCommand baseCommand);

}
