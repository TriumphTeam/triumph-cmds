package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
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
    public @NotNull <T> Optional<List<T>> getAsList(final @NotNull String name, final @NotNull Class<T> type) {
        final List<T> value = (List<T>) values.get(name);
        return Optional.ofNullable(value);
    }

    @Override
    public @NotNull <T> Optional<Set<T>> getAsSet(final @NotNull String name, final @NotNull Class<T> type) {
        final Set<T> value = (Set<T>) values.get(name);
        return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "values=" + values +
                '}';
    }
}
