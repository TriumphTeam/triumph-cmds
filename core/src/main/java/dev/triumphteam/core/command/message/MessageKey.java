package dev.triumphteam.core.command.message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MessageKey {

    public static final MessageKey WRONG_USAGE = of("wrong.usage");
    public static final MessageKey INVALID_COMMAND = of("invalid.command");

    private final String key;

    private MessageKey(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    @Contract("_ -> new")
    public static MessageKey of(@NotNull final String key) {
        return new MessageKey(key);
    }

    @NotNull
    public String getValue() {
        return key;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MessageKey that = (MessageKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}
