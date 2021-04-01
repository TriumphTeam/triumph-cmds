package dev.triumphteam.core.internal;

import org.jetbrains.annotations.NotNull;

public interface Command {

    boolean addSubCommands(@NotNull final CommandBase commandBase);

}
