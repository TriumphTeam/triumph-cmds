package dev.triumphteam.cmd.core.extention.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class CommandMeta {

    private final Map<MetaKey<?>, Object> dataMap = new HashMap<>();

    private final CommandMeta parentMeta;

    public CommandMeta(final @Nullable CommandMeta parentMeta) {
        this.parentMeta = parentMeta;
    }

    public <V> void add(final @NotNull MetaKey<V> metaKey, final @NotNull V value) {
        dataMap.put(metaKey, value);
    }

    @SuppressWarnings("unchecked")
    public <V> @Nullable V get(final @NotNull MetaKey<V> metaKey) {
        return (V) dataMap.get(metaKey);
    }

    public <V> boolean isPresent(final @NotNull MetaKey<V> metaKey) {
        return dataMap.containsKey(metaKey);
    }

    public @Nullable CommandMeta getParentMeta() {
        return parentMeta;
    }
}
