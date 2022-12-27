package dev.triumphteam.cmd.core.extention.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface CommandMeta {

    @SuppressWarnings("unchecked")
    <V> @Nullable V get(final @NotNull MetaKey<V> metaKey);

    <V> boolean isPresent(final @NotNull MetaKey<V> metaKey);

    public @Nullable CommandMeta getParentMeta();

    final class Builder {
        private final Map<MetaKey<?>, Object> dataMap = new HashMap<>();

        private final CommandMeta parentMeta;

        public Builder(final @Nullable CommandMeta parentMeta) {
            this.parentMeta = parentMeta;
        }

        public <V> void add(final @NotNull MetaKey<V> metaKey, final @Nullable V value) {
            dataMap.put(metaKey, value);
        }

        public <V> void add(final @NotNull MetaKey<V> metaKey) {
            add(metaKey, null);
        }

        public @Nullable CommandMeta getParentMeta() {
            return parentMeta;
        }

        public CommandMeta build() {
            return new ImmutableCommandMeta(parentMeta, Collections.unmodifiableMap(dataMap));
        }
    }
}
