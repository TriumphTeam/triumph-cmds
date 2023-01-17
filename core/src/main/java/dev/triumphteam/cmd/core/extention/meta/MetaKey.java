package dev.triumphteam.cmd.core.extention.meta;

import dev.triumphteam.cmd.core.extention.StringKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Identifier for a specific meta value.
 *
 * @param <V> The type of the meta value.
 */
public final class MetaKey<V> extends StringKey {

    public static final MetaKey<String> NAME = new MetaKey<>("command.name", String.class);
    public static final MetaKey<String> DESCRIPTION = new MetaKey<>("command.description", String.class);

    private final Class<V> valueType;

    private MetaKey(
            final @NotNull String key,
            final @NotNull Class<V> valueType
    ) {
        super(key);

        this.valueType = valueType;
    }

    /**
     * Factory method for creating a {@link MetaKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link MetaKey}.
     */
    @Contract("_, _ -> new")
    public static <V> @NotNull MetaKey<V> of(final @NotNull String key, final @NotNull Class<V> valueType) {
        return new MetaKey<>(key, valueType);
    }

    @Override
    public @NotNull String toString() {
        return "MetaKey{super=" + super.toString() + "}";
    }
}
