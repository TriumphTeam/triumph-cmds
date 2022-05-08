package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import org.jetbrains.annotations.NotNull;

public class SpongeSubCommand<S> extends AbstractSubCommand<S> {
    public SpongeSubCommand(@NotNull AbstractSubCommandProcessor<S> processor, @NotNull String parentName, @NotNull ExecutionProvider executionProvider) {
        super(processor, parentName, executionProvider);
    }
}
