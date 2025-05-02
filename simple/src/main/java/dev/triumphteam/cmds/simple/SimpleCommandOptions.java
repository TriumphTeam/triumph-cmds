package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

public final class SimpleCommandOptions<S> extends CommandOptions<S, S, SimpleCommandOptions<S>, String> {
    public SimpleCommandOptions(final @NotNull SenderExtension<S, S> senderExtension, final @NotNull Builder<S, S, SimpleCommandOptions<S>, ?, String> builder) {
        super(senderExtension, builder);
    }
}
