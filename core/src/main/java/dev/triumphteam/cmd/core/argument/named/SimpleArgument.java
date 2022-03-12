package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimpleArgument implements Argument {

    private final Class<?> type;
    private final String name;
    private final String description;
    private final SuggestionKey suggestionKey;


    public SimpleArgument(@NotNull final AbstractArgumentBuilder<?> argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return type;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public SuggestionKey getSuggestion() {
        return suggestionKey;
    }
}
