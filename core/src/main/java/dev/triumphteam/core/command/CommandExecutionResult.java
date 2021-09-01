package dev.triumphteam.core.command;

import org.jetbrains.annotations.NotNull;

public enum CommandResult {

    SUCCESS(""),
    WRONG_USAGE("");

    private final String key;

    CommandResult(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String messageKey() {
        return key;
    }
}
