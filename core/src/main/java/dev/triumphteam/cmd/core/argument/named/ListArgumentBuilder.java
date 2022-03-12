package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ListArgumentBuilder extends AbstractArgumentBuilder<ListArgumentBuilder> {

    private final Class<?> collectionType;
    private String separator = ",";

    public ListArgumentBuilder(@NotNull final Class<?> collectionType, @NotNull final Class<?> type) {
        super(type);
        this.collectionType = collectionType;
    }

    public ListArgumentBuilder separator(@NotNull final String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Builds the argument.
     *
     * @return A new {@link Argument} with the data from this builder.
     */
    @NotNull
    @Contract(" -> new")
    public Argument build() {
        return new ListArgument(this);
    }

    Class<?> getCollectionType() {
        return collectionType;
    }

    @NotNull
    String getSeparator() {
        return separator;
    }
}
