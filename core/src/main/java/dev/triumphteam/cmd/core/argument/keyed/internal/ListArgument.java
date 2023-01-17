/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.argument.keyed.internal;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ListArgument implements Argument {

    private final Class<?> collectionType;
    private final String separator;
    private final Class<?> type;
    private final String name;
    private final String description;
    private final SuggestionKey suggestionKey;


    public ListArgument(final @NotNull Argument.CollectionBuilder argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
        this.collectionType = argumentBuilder.getCollectionType();
        this.separator = argumentBuilder.getSeparator();
    }

    @Override
    public @NotNull Class<?> getType() {
        return type;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @Nullable SuggestionKey getSuggestion() {
        return suggestionKey;
    }

    public @NotNull Class<?> getCollectionType() {
        return collectionType;
    }

    public @NotNull String getSeparator() {
        return separator;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ListArgument that = (ListArgument) o;
        return collectionType.equals(that.collectionType) && separator.equals(that.separator) && type.equals(that.type) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionType, separator, type, name);
    }
}
