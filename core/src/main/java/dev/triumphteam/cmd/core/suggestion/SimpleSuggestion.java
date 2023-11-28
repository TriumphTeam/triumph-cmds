/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SimpleSuggestion<S> implements Suggestion<S> {

    private final SuggestionResolver<S> resolver;
    private final SuggestionMethod method;

    public SimpleSuggestion(
            final @NotNull SuggestionResolver<S> resolver,
            final @NotNull SuggestionMethod method
    ) {
        this.resolver = resolver;
        this.method = method;
    }

    @Override
    public @NotNull List<String> getSuggestions(
            final @NotNull S sender,
            final @NotNull String current,
            final @NotNull List<String> arguments
    ) {
        Stream<String> stream = resolver.resolve(sender, arguments).stream();

        switch (method) {
            case STARTS_WITH: {
                stream = stream.filter(it -> it.toLowerCase().startsWith(current.toLowerCase()));
                break;
            }

            case CONTAINS: {
                stream = stream.filter(it -> it.toLowerCase().contains(current.toLowerCase()));
                break;
            }

            default: break;
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleSuggestion<?> that = (SimpleSuggestion<?>) o;
        return resolver.equals(that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolver);
    }

    @Override
    public @NotNull String toString() {
        return "SimpleSuggestion{" +
                "resolver=" + resolver +
                '}';
    }
}
