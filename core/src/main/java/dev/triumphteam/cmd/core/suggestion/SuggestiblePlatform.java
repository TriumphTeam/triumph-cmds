package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

public interface SuggestiblePlatform {

    void registerSuggestion(@NotNull final SuggestionKey key, @NotNull final SuggestionResolver suggestionResolver);

}
