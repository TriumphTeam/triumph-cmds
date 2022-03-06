package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class SuggestionContext {

    private final List<String> args;
    private final String command;
    private final String subCommand;

    public SuggestionContext(
            @NotNull final List<String> args,
            @NotNull final String command,
            @NotNull final String subCommand
    ) {
        this.args = args;
        this.command = command;
        this.subCommand = subCommand;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public String getCommand() {
        return command;
    }

    public String getSubCommand() {
        return subCommand;
    }
}
