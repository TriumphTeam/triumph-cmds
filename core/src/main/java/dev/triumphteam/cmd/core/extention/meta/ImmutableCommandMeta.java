package dev.triumphteam.cmd.core.extention.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
final class ImmutableCommandMeta implements CommandMeta {

    private final CommandMeta parentMeta;
    private final Map<MetaKey<?>, Object> meta;

    public ImmutableCommandMeta(
            final @Nullable CommandMeta parentMeta,
            final @NotNull Map<MetaKey<?>, Object> meta
    ) {
        this.parentMeta = parentMeta;
        this.meta = meta;
    }

    @Override
    public @NotNull <V> Optional<V> get(final @NotNull MetaKey<V> metaKey) {
        return Optional.ofNullable(getNullable(metaKey));
    }

    @Override
    public <V> @Nullable V getNullable(final @NotNull MetaKey<V> metaKey) {
        return (V) meta.get(metaKey);
    }

    @Override
    public <V> V getOrDefault(final @NotNull MetaKey<V> metaKey, @Nullable final V def) {
        return (V) meta.getOrDefault(metaKey, def);
    }

    @Override
    public <V> boolean isPresent(final @NotNull MetaKey<V> metaKey) {
        return meta.containsKey(metaKey);
    }

    @Override
    public @Nullable CommandMeta getParentMeta() {
        return parentMeta;
    }

    @Override
    public String toString() {
        return "ImmutableCommandMeta{" +
                "meta=" + meta +
                '}';
    }
}
