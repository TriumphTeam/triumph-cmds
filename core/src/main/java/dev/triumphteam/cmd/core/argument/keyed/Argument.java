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

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * An argument or "named" argument, is separate from the {@link InternalArgument}, as they are used within one.
 * This is used for explicit name to value arguments, for example <code>name:John</code>.
 */
public interface Argument {

    /**
     * Creates a builder for a {@link String} argument.
     *
     * @return A {@link Builder} for a {@link String} argument.
     */
    @Contract(" -> new")
    static @NotNull Argument.Builder forString() {
        return new Builder(String.class);
    }

    /**
     * Creates a builder for a {@link Integer} argument.
     *
     * @return A {@link Builder} for a {@link Integer} argument.
     */
    @Contract(" -> new")
    static @NotNull Argument.Builder forInt() {
        return new Builder(int.class);
    }

    /**
     * Creates a builder for a {@link Float} argument.
     *
     * @return A {@link Builder} for a {@link Float} argument.
     */
    @Contract(" -> new")
    static @NotNull Argument.Builder forFloat() {
        return new Builder(float.class);
    }

    /**
     * Creates a builder for a {@link Double} argument.
     *
     * @return A {@link Builder} for a {@link Double} argument.
     */
    @Contract(" -> new")
    static @NotNull Argument.Builder forDouble() {
        return new Builder(double.class);
    }

    /**
     * Creates a builder for a {@link Boolean} argument.
     *
     * @return A {@link Builder} for a {@link Boolean} argument.
     */
    @Contract(" -> new")
    static @NotNull Argument.Builder forBoolean() {
        return new Builder(boolean.class);
    }

    /**
     * Creates a builder for a custom type argument.
     *
     * @return A {@link Builder} for a custom type argument.
     */
    @Contract("_ -> new")
    static @NotNull Argument.Builder forType(final @NotNull Class<?> type) {
        return new Builder(type);
    }

    /**
     * Creates a {@link List} builder of a custom type argument.
     *
     * @param type The type of the argument.
     * @return A {@link CollectionBuilder} for a custom type argument.
     */
    @Contract("_ -> new")
    static @NotNull Argument.CollectionBuilder listOf(final @NotNull Class<?> type) {
        return new CollectionBuilder(List.class, type);
    }

    /**
     * Creates a {@link Set} builder of a custom type argument.
     *
     * @param type The type of the argument.
     * @return A {@link CollectionBuilder} for a custom type argument.
     */
    @Contract("_ -> new")
    static @NotNull Argument.CollectionBuilder setOf(final @NotNull Class<?> type) {
        return new CollectionBuilder(Set.class, type);
    }

    /**
     * @return The type of the argument.
     */
    @NotNull Class<?> getType();

    /**
     * @return The name of the argument.
     */
    @NotNull String getName();

    /**
     * @return The description of the argument.
     */
    @NotNull String getDescription();

    /**
     * @return The {@link SuggestionKey} to be used.
     */
    @Nullable SuggestionKey getSuggestion();

    /**
     * The default builder, simply extends the functionality of {@link AbstractBuilder}.
     */
    class Builder extends AbstractBuilder<Builder> {
        public Builder(final @NotNull Class<?> type) {
            super(type);
        }
    }

    /**
     * A builder for specific collections.
     * Adds the collection type and the separator to be used during execution.
     */
    final class CollectionBuilder extends AbstractBuilder<CollectionBuilder> {

        private final Class<?> collectionType;
        private String separator = ",";

        public CollectionBuilder(final @NotNull Class<?> collectionType, final @NotNull Class<?> type) {
            super(type);
            this.collectionType = collectionType;
        }

        /**
         * Set the separator to be used to split an argument into a collection.
         *
         * @param separator The char or string to use as separator, supports regex.
         * @return This builder.
         */
        @Contract("_ -> this")
        public @NotNull Argument.CollectionBuilder separator(final @NotNull String separator) {
            this.separator = separator;
            return this;
        }

        /**
         * Builds the argument.
         *
         * @return A new {@link Argument} with the data from this builder.
         */
        @Contract(" -> new")
        public @NotNull Argument build() {
            return new ListArgument(this);
        }

        @NotNull Class<?> getCollectionType() {
            return collectionType;
        }

        @NotNull String getSeparator() {
            return separator;
        }
    }

    @SuppressWarnings("unchecked")
    abstract class AbstractBuilder<T extends AbstractBuilder<T>> {

        private final Class<?> type;

        private String name;
        private String description;
        private SuggestionKey suggestionKey;

        private AbstractBuilder(final @NotNull Class<?> type) {
            this.type = type;
        }

        /**
         * Sets the name of the argument.
         *
         * @param name The name of the argument.
         * @return This builder.
         */
        @Contract("_ -> this")
        public @NotNull T name(final @NotNull String name) {
            this.name = name;
            return (T) this;
        }

        /**
         * Sets the description of the argument.
         *
         * @param description The description of the argument.
         * @return This builder.
         */
        @Contract("_ -> this")
        public @NotNull T description(final @NotNull String description) {
            this.description = description;
            return (T) this;
        }

        /**
         * Sets the suggestion key to be used by the argument.
         * If not are supplied the argument will use {@link EmptySuggestion} instead.
         *
         * @param suggestionKey The registered suggestion key.
         * @return This builder.
         */
        @Contract("_ -> this")
        public @NotNull T suggestion(final @NotNull SuggestionKey suggestionKey) {
            this.suggestionKey = suggestionKey;
            return (T) this;
        }

        /**
         * Builds the argument.
         *
         * @return A new {@link Argument} with the data from this builder.
         */
        @Contract(" -> new")
        public @NotNull Argument build() {
            return new SimpleArgument(this);
        }

        @NotNull Class<?> getType() {
            return type;
        }

        @NotNull String getName() {
            if (name == null || name.isEmpty()) {
                throw new CommandRegistrationException("Argument is missing a name!");
            }
            return name;
        }

        @NotNull String getDescription() {
            return description;
        }

        @Nullable SuggestionKey getSuggestionKey() {
            return suggestionKey;
        }
    }
}
