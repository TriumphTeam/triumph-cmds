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
package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class SimpleArgument implements Argument {

    private final String name;
    private final String longName;
    private final Class<?> type;
    private final String description;
    private final SuggestionKey suggestionKey;
    private final boolean isLongNameArgument;

    public SimpleArgument(final @NotNull Argument.AbstractBuilder<?> argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.longName = argumentBuilder.getLongName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
        this.isLongNameArgument = false;
    }

    public SimpleArgument(
            final String name,
            final String longName,
            final Class<?> type,
            final String description,
            final SuggestionKey suggestionKey,
            final boolean isLongNameArgument
    ) {
        this.name = name;
        this.longName = longName;
        this.type = type;
        this.description = description;
        this.suggestionKey = suggestionKey;
        this.isLongNameArgument = isLongNameArgument;
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
    public @Nullable String getLongName() {
        return longName;
    }

    @Override
    public @NotNull Argument asLongNameArgument() {
        return new SimpleArgument(name, longName, type, description, suggestionKey, true);
    }

    @Override
    public boolean isLongNameArgument() {
        return isLongNameArgument;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @Nullable SuggestionKey getSuggestion() {
        return suggestionKey;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleArgument that = (SimpleArgument) o;
        return Objects.equals(name, that.name) && Objects.equals(longName, that.longName) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(suggestionKey, that.suggestionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, longName, type, description, suggestionKey);
    }

    @Override
    public String toString() {
        return "SimpleArgument{" +
                "suggestionKey=" + suggestionKey +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", longName='" + longName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
