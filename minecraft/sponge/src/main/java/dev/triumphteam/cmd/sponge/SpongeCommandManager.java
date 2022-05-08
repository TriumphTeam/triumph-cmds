package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;

public class SpongeCommandManager<S> extends CommandManager<S, CommandCause> {
    public SpongeCommandManager(@NotNull SenderMapper<S, CommandCause> senderMapper, @NotNull SenderValidator<CommandCause> senderValidator) {
        super(senderMapper, senderValidator);
    }

    @Override
    public void registerCommand(@NotNull BaseCommand baseCommand) {

    }

    @Override
    public void unregisterCommand(@NotNull BaseCommand command) {

    }

    @Override
    protected @NotNull RegistryContainer<CommandCause> getRegistryContainer() {
        return null;
    }
}
