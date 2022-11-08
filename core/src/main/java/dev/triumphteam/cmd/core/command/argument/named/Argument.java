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
package dev.triumphteam.cmd.core.command.argument.named;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface Argument {

    /**
     * Creates a builder for a {@link String} argument.
     *
     * @return A {@link ArgumentBuilder} for a {@link String} argument.
     */
    static @NotNull ArgumentBuilder forString() {
        return new ArgumentBuilder(String.class);
    }

    /**
     * Creates a builder for a {@link Integer} argument.
     *
     * @return A {@link ArgumentBuilder} for a {@link Integer} argument.
     */
    static @NotNull ArgumentBuilder forInt() {
        return new ArgumentBuilder(int.class);
    }

    /**
     * Creates a builder for a {@link Float} argument.
     *
     * @return A {@link ArgumentBuilder} for a {@link Float} argument.
     */
    static @NotNull ArgumentBuilder forFloat() {
        return new ArgumentBuilder(float.class);
    }

    /**
     * Creates a builder for a {@link Double} argument.
     *
     * @return A {@link ArgumentBuilder} for a {@link Double} argument.
     */
    static @NotNull ArgumentBuilder forDouble() {
        return new ArgumentBuilder(double.class);
    }

    /**
     * Creates a builder for a {@link Boolean} argument.
     *
     * @return A {@link ArgumentBuilder} for a {@link Boolean} argument.
     */
    static @NotNull ArgumentBuilder forBoolean() {
        return new ArgumentBuilder(boolean.class);
    }

    /**
     * Creates a builder for a custom type argument.
     *
     * @return A {@link ArgumentBuilder} for a custom type argument.
     */
    static @NotNull ArgumentBuilder forType(final @NotNull Class<?> type) {
        return new ArgumentBuilder(type);
    }

    static @NotNull ListArgumentBuilder listOf(final @NotNull Class<?> type) {
        return new ListArgumentBuilder(List.class, type);
    }

    static @NotNull ListArgumentBuilder setOf(final @NotNull Class<?> type) {
        return new ListArgumentBuilder(Set.class, type);
    }

    // TODO: Comments
    @NotNull Class<?> getType();

    @NotNull String getName();

    @NotNull String getDescription();

    @Nullable SuggestionKey getSuggestion();
}
