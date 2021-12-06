package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class EmptySuggestion implements Suggestion {

    public static final Suggestion INSTANCE = new EmptySuggestion();

    @NotNull
    @Override
    public List<String> getSuggestions() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "EmptySuggestion{}";
    }
}
