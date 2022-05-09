package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandCause;

import java.lang.reflect.Method;

//extends AbstractCommandProcessor<CommandSender, S, BukkitSubCommand<S>, BukkitSubCommandProcessor<S>> {
public class SpongeCommandProcessor<S> extends AbstractCommandProcessor<CommandCause, S, SpongeSubCommand<S>, SpongeSubCommandProcessor<S>> {
    protected SpongeCommandProcessor(@NotNull BaseCommand baseCommand, @NotNull RegistryContainer registryContainer, @NotNull SenderMapper senderMapper, @NotNull SenderValidator senderValidator, @NotNull ExecutionProvider syncExecutionProvider, @NotNull ExecutionProvider asyncExecutionProvider) {
        super(baseCommand, registryContainer, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);
    }

    @Override
    protected @NotNull SpongeSubCommandProcessor<S> createProcessor(@NotNull Method method) {
        return null;
    }

    @Override
    protected @Nullable SpongeSubCommand<S> createSubCommand(@NotNull SpongeSubCommandProcessor<S> processor, @NotNull ExecutionProvider executionProvider) {
        return null;
    }
}
