package dev.triumphteam.cmd.core.execution;

import org.jetbrains.annotations.NotNull;

/**
 * Provides different ways to execute the command.
 */
public interface ExecutionProvider {

    /**
     * Executes the command.
     * This can be done in many forms, such as synchronous or asynchronous.
     * This also allows for different asynchronous implementations, for example, <code>BukkitScheduler</code> or <code>CompletableFuture</code>.
     *
     * @param command The command to execute.
     */
    void execute(@NotNull final Runnable command);

}
