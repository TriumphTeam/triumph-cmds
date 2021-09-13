package dev.triumphteam.cmds.core.key;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class RegistryKey {

    private final String key;

    public RegistryKey(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RegistryKey that = (RegistryKey) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "RegistryKey{" +
                "key='" + key + '\'' +
                '}';
    }

}
