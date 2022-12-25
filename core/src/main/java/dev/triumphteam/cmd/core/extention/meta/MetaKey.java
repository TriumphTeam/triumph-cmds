package dev.triumphteam.cmd.core.extention.meta;

import dev.triumphteam.cmd.core.extention.StringKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MetaKey<V> extends StringKey {

    private final Class<V> valueType;

    private MetaKey(
            final @NotNull String key,
            final @NotNull Class<V> valueType
    ) {
        super(key);

        this.valueType = valueType;
    }

    @Contract("_, _ -> new")
    public static <V> @NotNull MetaKey<V> of(final @NotNull String key, final @NotNull Class<V> valueType) {
        return new MetaKey<>(key, valueType);
    }
}
