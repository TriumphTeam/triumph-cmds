package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;

public final class SpongeCommandManager<S> extends CommandManager<CommandCause, S> {

    private final PluginContainer plugin;

    private final RegistryContainer<S> registryContainer = new RegistryContainer<>();

    private final Map<String, SpongeCommand<S>> commands = new HashMap<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider;


    public SpongeCommandManager(
            @NotNull final PluginContainer plugin,
            @NotNull SenderMapper<CommandCause, S> senderMapper,
            @NotNull SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
        this.plugin = plugin;
        this.asyncExecutionProvider = new SpongeAsyncExecutionProvider(plugin);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final SpongeCommandProcessor<S> processor = new SpongeCommandProcessor<>(
                baseCommand,
                registryContainer,
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider
        );

    }

    @Override
    public void unregisterCommand(@NotNull BaseCommand command) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }
}
