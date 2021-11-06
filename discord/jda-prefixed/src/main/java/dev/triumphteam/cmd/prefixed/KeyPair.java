package dev.triumphteam.cmd.prefixed;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class KeyPair<F, S> {

    private final F first;
    private final S second;

    private KeyPair(@NotNull final F first, @NotNull final S second) {
        this.first = first;
        this.second = second;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static <F, S> KeyPair<F, S> of(@NotNull final F first, @NotNull final S second) {
        return new KeyPair<>(first, second);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KeyPair<?, ?> keyPair = (KeyPair<?, ?>) o;
        return first.equals(keyPair.first) && second.equals(keyPair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "KeyPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
