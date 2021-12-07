package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.triumphteam.cmd.core.util.EnumUtils.getEnumConstants;
import static dev.triumphteam.cmd.core.util.EnumUtils.populateCache;

public final class EnumSuggestion implements Suggestion {

    private final Class<? extends Enum<?>> enumType;

    public EnumSuggestion(@NotNull final Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;

        populateCache(enumType);
    }

    @NotNull
    @Override
    public List<String> getSuggestions() {
        return getEnumConstants(enumType)
                .values()
                .stream()
                .map(it -> {
                    final Enum<?> constant = it.get();
                    if (constant == null) return null;
                    return constant.name();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EnumSuggestion that = (EnumSuggestion) o;
        return enumType.equals(that.enumType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumType);
    }

    @NotNull
    @Override
    public String toString() {
        return "EnumSuggestion{" +
                "enumType=" + enumType +
                '}';
    }
}
