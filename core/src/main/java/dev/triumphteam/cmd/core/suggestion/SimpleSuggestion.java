package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class SimpleSuggestion implements Suggestion {

    private final SuggestionResolver resolver;

    public SimpleSuggestion(@NotNull final SuggestionResolver resolver) {
        this.resolver = resolver;
    }

    @NotNull
    @Override
    public List<String> getSuggestions() {
        return resolver.resolve();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleSuggestion that = (SimpleSuggestion) o;
        return resolver.equals(that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolver);
    }

    @Override
    public String toString() {
        return "SimpleSuggestion{" +
                "resolver=" + resolver +
                '}';
    }
}
