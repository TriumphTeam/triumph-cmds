package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;

import java.lang.reflect.Method;

final class SpongeCommandProcessor<S> extends AbstractCommandProcessor<CommandCause, S, SpongeSubCommand<S>, SpongeSubCommandProcessor<S>> {
    public SpongeCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final RegistryContainer<S> registryContainer,
            @NotNull final SenderMapper<CommandCause, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator,
            @NotNull final ExecutionProvider syncExecutionProvider,
            @NotNull final ExecutionProvider asyncExecutionProvider
    ) {
        super(baseCommand, registryContainer, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);
    }

    @Override
    protected @NotNull SpongeSubCommandProcessor<S> createProcessor(@NotNull final Method method) {
        return new SpongeSubCommandProcessor<>(
                getBaseCommand(),
                getName(),
                method,
                getRegistryContainer(),
                getSenderValidator()
        );
    }

    @Override
    protected SpongeSubCommand<S> createSubCommand(
            @NotNull SpongeSubCommandProcessor<S> processor,
            @NotNull ExecutionProvider executionProvider
    ) {
        return new SpongeSubCommand<>(processor, getName(), executionProvider);
    }
}
