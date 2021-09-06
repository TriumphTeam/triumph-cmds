package dev.triumphteam.core.command.message;

import org.jetbrains.annotations.NotNull;

public final class MessageContext<S> {

    private final S sender;

    public MessageContext(@NotNull final S sender) {
        this.sender = sender;
    }

}
