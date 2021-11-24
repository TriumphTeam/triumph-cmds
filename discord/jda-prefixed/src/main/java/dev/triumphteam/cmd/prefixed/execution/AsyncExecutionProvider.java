package dev.triumphteam.cmd.prefixed.execution;

import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class AsyncExecutionProvider implements ExecutionProvider {

    @Override
    public void execute(final @NotNull Runnable command) {
        CompletableFuture.runAsync(command);
    }
}
