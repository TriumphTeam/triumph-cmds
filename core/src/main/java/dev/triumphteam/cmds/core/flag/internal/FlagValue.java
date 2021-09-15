package dev.triumphteam.cmds.core.flag.internal;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Simple holder for the flag values.
 * Allows to reduce a bit the checks and having only one Map in the {@link dev.triumphteam.cmds.core.flag.Flags}.
 */
class FlagValue {

    private final Object value;
    private final Class<?> type;

    public FlagValue(@Nullable final Object value, @Nullable final Class<?> type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Gets the flag value.
     *
     * @return The flag value.
     */
    @Nullable
    public Object getValue() {
        return value;
    }

    /**
     * Gets the flag type.
     *
     * @return The flag type.
     */
    @Nullable
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FlagValue flagValue = (FlagValue) o;
        return Objects.equals(value, flagValue.value) && Objects.equals(type, flagValue.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "FlagValue{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }
}
