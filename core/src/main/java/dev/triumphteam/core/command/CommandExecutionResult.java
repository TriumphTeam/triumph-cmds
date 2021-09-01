package dev.triumphteam.core.command;

import org.jetbrains.annotations.NotNull;

public enum CommandExecutionResult {

    SUCCESS(""),
    WRONG_USAGE("cmd.wrong.usage"),
    NO_EXISTS("cmd.no.exists");

    private final String key;

    CommandExecutionResult(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String key() {
        return key;
    }

}
