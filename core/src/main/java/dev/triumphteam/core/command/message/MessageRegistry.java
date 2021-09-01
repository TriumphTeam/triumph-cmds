package dev.triumphteam.core.command.message;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class MessageRegistry<S> {

    private final Map<String, MessageResolver<S>> messages = new HashMap<>();

    public void register(@NotNull final String key, @NotNull final MessageResolver<S> resolver) {
        messages.put(key, resolver);
    }

    public void sendMessage(@NotNull final String key, @NotNull final S sender) {
        final MessageResolver<S> messageResolver = messages.get(key);
        if (messageResolver == null) return;
        messageResolver.resolve(sender);
    }

}
