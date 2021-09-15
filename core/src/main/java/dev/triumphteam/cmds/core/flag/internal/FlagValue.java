package dev.triumphteam.cmds.core.flag.internal;

import org.jetbrains.annotations.Nullable;

final class FlagValue {

    private final Object value;
    private final Class<?> type;

    public FlagValue(@Nullable final Object value, @Nullable final Class<?> type) {
        this.value = value;
        this.type = type;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Nullable
    public Class<?> getType() {
        return type;
    }

}
