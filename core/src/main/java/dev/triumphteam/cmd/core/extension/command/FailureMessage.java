package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

public final class FailureMessage<C extends MessageContext> {

    private final MessageKey<C> messageKey;
    private final C context;

    public FailureMessage(final @NotNull MessageKey<C> messageKey, final @NotNull C context) {
        this.messageKey = messageKey;
        this.context = context;
    }

    public static @NotNull <C extends MessageContext> FailureMessage<C> of(final @NotNull MessageKey<C> messageKey, final @NotNull C context) {
        return new FailureMessage<>(messageKey, context);
    }

    public @NotNull MessageKey<C> getMessageKey() {
        return messageKey;
    }

    public @NotNull C getContext() {
        return context;
    }
}
