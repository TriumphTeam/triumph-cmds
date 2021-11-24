package dev.triumphteam.cmd.core.execution;

import org.jetbrains.annotations.NotNull;

public final class SyncExecutionProvider implements ExecutionProvider {

    @Override
    public void execute(final @NotNull Runnable command) {
        command.run();
    }
}
