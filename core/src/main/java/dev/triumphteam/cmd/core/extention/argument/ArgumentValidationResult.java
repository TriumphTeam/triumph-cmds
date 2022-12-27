package dev.triumphteam.cmd.core.extention.argument;

import org.jetbrains.annotations.NotNull;

public interface ArgumentValidationResult {

    class Valid implements ArgumentValidationResult {}

    class Ignore implements ArgumentValidationResult {}

    class Invalid implements ArgumentValidationResult {

        private final String message;

        public Invalid(final @NotNull String message) {
            this.message = message;
        }

        public @NotNull String getMessage() {
            return message;
        }
    }
}
