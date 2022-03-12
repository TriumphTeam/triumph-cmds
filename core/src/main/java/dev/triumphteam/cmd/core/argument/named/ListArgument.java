package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ListArgument implements Argument {

    private final Class<?> collectionType;
    private final String separator;
    private final Class<?> type;
    private final String name;
    private final String description;
    private final SuggestionKey suggestionKey;


    public ListArgument(@NotNull final ListArgumentBuilder argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
        this.collectionType = argumentBuilder.getCollectionType();
        this.separator = argumentBuilder.getSeparator();
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

    @NotNull
    public Class<?> getCollectionType() {
        return collectionType;
    }

    @NotNull
    public String getSeparator() {
        return separator;
    }
}
