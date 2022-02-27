package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public final class NamedArgumentResult implements Arguments {

    private final Map<String, Object> values;

    public NamedArgumentResult(@NotNull final Map<String, Object> values) {
        this.values = values;
    }

    @NotNull
    @Override
    public <T> Optional<T> get(final @NotNull String name, final @NotNull Class<T> type) {
        return (Optional<T>) Optional.ofNullable(values.get(name));
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "values=" + values +
                '}';
    }
}
