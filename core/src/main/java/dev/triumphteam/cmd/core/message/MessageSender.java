package dev.triumphteam.cmd.core.message;

import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

public interface MessageSender<S> {

    <C extends MessageContext> void sendMessage(
            final @NotNull MessageKey<C> key,
            final @NotNull S sender,
            final @NotNull C context
    );
}
