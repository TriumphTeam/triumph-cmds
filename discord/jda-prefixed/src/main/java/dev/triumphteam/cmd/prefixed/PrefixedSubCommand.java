package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import org.jetbrains.annotations.NotNull;

final class PrefixedSubCommand<S> extends AbstractSubCommand<S> {

    public PrefixedSubCommand(
            @NotNull final AbstractSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);
    }
}
