package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Suggestion {

    @NotNull
    List<String> getSuggestions();

}
