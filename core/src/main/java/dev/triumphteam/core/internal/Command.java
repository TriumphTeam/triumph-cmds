package dev.triumphteam.core.internal;

import org.jetbrains.annotations.NotNull;

public interface Command {

    void addSubCommands(@NotNull final CommandBase commandBase);

}
