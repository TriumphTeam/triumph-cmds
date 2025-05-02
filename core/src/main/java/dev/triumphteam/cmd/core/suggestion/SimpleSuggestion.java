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

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class SimpleSuggestion<S, ST> implements InternalSuggestion<S, ST> {

    private final SuggestionResolver<S, ST> resolver;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;

    public SimpleSuggestion(
            final @NotNull SuggestionResolver<S, ST> resolver,
            final @NotNull SuggestionMapper<ST> mapper,
            final @NotNull SuggestionMethod method
    ) {
        this.resolver = resolver;
        this.mapper = mapper;
        this.method = method;
    }

    @Override
    public @NotNull List<ST> getSuggestions(
            final @NotNull S sender,
            final @NotNull String current,
            final @NotNull List<String> arguments
    ) {
        return mapper.filter(current, resolver.resolve(new SuggestionContext<>(current, sender, arguments)), method);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleSuggestion<?, ?> that = (SimpleSuggestion<?, ?>) o;
        return Objects.equals(resolver, that.resolver) && Objects.equals(mapper, that.mapper) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolver, mapper, method);
    }

    @Override
    public @NotNull String toString() {
        return "SimpleSuggestion{" +
                "resolver=" + resolver +
                ", mapper=" + mapper +
                ", method=" + method +
                '}';
    }
}
