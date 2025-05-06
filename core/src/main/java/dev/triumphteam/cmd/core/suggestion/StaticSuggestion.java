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
package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class StaticSuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    private final SimpleSuggestionHolder.Static<ST> holder;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;

    public StaticSuggestion(
            SimpleSuggestionHolder.Static<ST> holder,
            final @NotNull SuggestionMapper<ST> mapper,
            final @NotNull SuggestionMethod method
    ) {
        this.holder = holder;
        this.mapper = mapper;
        this.method = method;
    }

    @Override
    public @NotNull List<ST> getSuggestions(
            final @NotNull S sender,
            final @NotNull String current,
            final @NotNull List<String> arguments,
            final @NotNull Map<String, String> argumentsMap
    ) {
        return mapper.filter(current, getSuggestions(), method);
    }

    public boolean contains(final @NotNull String suggestion) {
        return holder.contains(suggestion);
    }

    public @NotNull List<ST> getSuggestions() {
        return holder.getSuggestions();
    }
}
