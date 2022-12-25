package dev.triumphteam.cmd.core.extention.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

final class ImmutableCommandMeta implements CommandMeta {

    private final Map<MetaKey<?>, Object> meta;

    public ImmutableCommandMeta(final @NotNull Map<MetaKey<?>, Object> meta) {
        this.meta = meta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> @Nullable V get(final @NotNull MetaKey<V> metaKey) {
        return (V) meta.get(metaKey);
    }

    @Override
    public <V> boolean isPresent(final @NotNull MetaKey<V> metaKey) {
        return meta.containsKey(metaKey);
    }
}
