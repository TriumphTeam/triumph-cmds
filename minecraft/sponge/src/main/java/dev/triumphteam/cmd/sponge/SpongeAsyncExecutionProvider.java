package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

public final class SpongeAsyncExecutionProvider implements ExecutionProvider {

    private final PluginContainer plugin;

    public SpongeAsyncExecutionProvider(@NotNull final PluginContainer plugin) { this.plugin = plugin; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull Runnable command) {
        Sponge.asyncScheduler().executor(plugin).submit(command);
    }
}
