package dev.triumphteam.cmd.core.util;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

        extractSuggestionFromParams(suggestionRegistry, method, suggestionList, commandClass);

        return suggestionList;
    }

    private static void extractSuggestionFromParams(
            @NotNull final SuggestionRegistry suggestionRegistry,
            @NotNull final Method method,
            @NotNull final List<Suggestion> suggestionList,
            @NotNull final Class<? extends BaseCommand> commandClass
    ) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();

            final Suggestions suggestions = parameter.getAnnotation(Suggestions.class);
            final String suggestionKey = suggestions == null ? "" : suggestions.value()[0];
            if (suggestionKey.isEmpty() || suggestionKey.equals("enum")) {
                if (Enum.class.isAssignableFrom(type)) {
                    setOrAdd(suggestionList, i, new EnumSuggestion((Class<? extends Enum<?>>) type));
                    continue;
                }

                setOrAdd(suggestionList, i, EmptySuggestion.INSTANCE);
                continue;
            }

            final SuggestionResolver resolver = suggestionRegistry.getSuggestion(SuggestionKey.of(suggestionKey));
            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + suggestionKey + "`", method, commandClass);
            }
            setOrAdd(suggestionList, i, new SimpleSuggestion(resolver));
        }
    }

    private static void setOrAdd(@NotNull final List<Suggestion> suggestionList, final int index, @NotNull final Suggestion suggestion) {
        if (suggestion instanceof EmptySuggestion) return;

        if (index >= suggestionList.size()) {
            suggestionList.add(suggestion);
            return;
        }

        suggestionList.set(index, suggestion);
    }

}
