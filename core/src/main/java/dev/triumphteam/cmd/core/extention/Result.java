package dev.triumphteam.cmd.core.extention;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Result<V, F> {

    default void fold(
            final @NotNull Consumer<V> onSuccess,
            final @NotNull Consumer<F> onFailure
    ) {
        if (this instanceof Success) {
            onSuccess.accept(((Success<V, F>) this).getValue());
            return;
        }

        if (this instanceof Failure) {
            onFailure.accept(((Failure<V, F>) this).getFail());
        }
    }

    final class Success<V, F> implements Result<V, F> {
        private final V value;

        public Success(final @NotNull V value) {
            this.value = value;
        }

        public @NotNull V getValue() {
            return value;
        }
    }

    final class Failure<V, F> implements Result<V, F> {
        private final F fail;

        public Failure(final @NotNull F fail) {
            this.fail = fail;
        }

        public @NotNull F getFail() {
            return fail;
        }
    }
}

