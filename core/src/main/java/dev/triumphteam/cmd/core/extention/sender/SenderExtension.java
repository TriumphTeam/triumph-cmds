package dev.triumphteam.cmd.core.extention.sender;

import dev.triumphteam.cmd.core.command.ExecutableCommand;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface SenderExtension<D, S> {

    @NotNull Set<Class<? extends S>> getAllowedSenders();

    boolean validate(
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull ExecutableCommand<S> command,
            final @NotNull S sender
    );

    @Nullable S map(final @NotNull D defaultSender);
}
