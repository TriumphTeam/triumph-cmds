package dev.triumphteam.cmd.core.util;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlatformUtils {

    private PlatformUtils() {throw new AssertionError("Util must not be initialized");}

    public static List<Suggestion> extractSuggestions(
            @NotNull final SuggestionRegistry suggestionRegistry,
            @NotNull final Method method,
            @NotNull final Class<? extends BaseCommand> commandClass
    ) {
        final Suggestions suggestions = method.getAnnotation(Suggestions.class);
        if (suggestions == null) return Collections.emptyList();

        final List<Suggestion> suggestionList = new ArrayList<>();

        for (final String key : suggestions.value()) {

            if (key.isEmpty()) {
                suggestionList.add(EmptySuggestion.INSTANCE);
                continue;
            }

            final SuggestionResolver resolver = suggestionRegistry.getSuggestion(SuggestionKey.of(key));

            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + key + "`", method, commandClass);
            }

            suggestionList.add(new SimpleSuggestion(resolver));
        }

        return suggestionList;
    }

}
