package dev.triumphteam.cmd.core.execution;

import org.jetbrains.annotations.NotNull;

public interface ExecutionProvider {

    void execute(@NotNull final Runnable command);

}
