package dev.triumphteam.cmd.prefixed.execution;

import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Prefixed JDA platform's implementation of asynchronous execution.
 */
public final class AsyncExecutionProvider implements ExecutionProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final @NotNull Runnable command) {
        CompletableFuture.runAsync(command);
    }
}
