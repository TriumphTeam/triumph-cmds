package dev.triumphteam.core.internal;

import org.jetbrains.annotations.NotNull;

public interface Command {

    void addSubCommand(@NotNull final CommandBase commandBase);

}
