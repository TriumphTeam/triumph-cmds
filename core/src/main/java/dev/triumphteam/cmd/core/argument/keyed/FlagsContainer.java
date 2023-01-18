package dev.triumphteam.cmd.core.argument.keyed;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
abstract class FlagsContainer implements Arguments {

    private final Map<String, ArgumentValue> flags;

    public FlagsContainer(final @NotNull Map<String, ArgumentValue> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasFlag(final @NotNull String flag) {
        return flags.containsKey(flag);
    }

    @Override
    public @NotNull <T> Optional<T> getFlagValue(final @NotNull String flag, final @NotNull Class<T> type) {
        final ArgumentValue flagValue = flags.get(flag);
        if (flagValue == null) return Optional.empty();
        if (!(flagValue instanceof SimpleArgumentValue)) return Optional.empty();
        final SimpleArgumentValue argFlagValue = (SimpleArgumentValue) flagValue;
        return Optional.ofNullable((T) argFlagValue.getValue());
    }

    @Override
    public @NotNull Optional<String> getFlagValue(final @NotNull String flag) {
        final ArgumentValue flagValue = flags.get(flag);
        if (flagValue == null) return Optional.empty();
        if (!(flagValue instanceof SimpleArgumentValue)) return Optional.empty();
        final SimpleArgumentValue argFlagValue = (SimpleArgumentValue) flagValue;
        return Optional.of(argFlagValue.getAsString());
    }

    @Override
    public @NotNull Set<String> getAllFlags() {
        return flags.keySet();
    }

    @Override
    public boolean hasFlags() {
        return !flags.isEmpty();
    }

    @Override
    public String toString() {
        return "FlagsContainer{" +
                "flags=" + flags +
                '}';
    }
}
