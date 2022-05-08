package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SpongeSenderValidator<S> implements SenderValidator<S> {
    @Override
    public @NotNull Set<Class<? extends S>> getAllowedSenders() {
        return null;
    }

    @Override
    public boolean validate(@NotNull MessageRegistry<S> messageRegistry, @NotNull SubCommand<S> subCommand, @NotNull S sender) {
        return false;
    }
}
