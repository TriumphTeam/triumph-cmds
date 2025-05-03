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
package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class DefaultSuggestionMapper implements SuggestionMapper<String> {

    @Override
    public @NotNull List<String> map(final @NotNull List<String> values, final @NotNull Class<?> type) {
        return values;
    }

    @Override
    public @NotNull List<String> mapBackwards(final @NotNull List<String> values) {
        return values;
    }

    @Override
    public @NotNull List<String> filter(final @NotNull String input, final @NotNull List<String> values, final SuggestionMethod method) {
        switch (method) {
            case STARTS_WITH:
                return values.stream().filter(it -> it.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());

            case CONTAINS:
                return values.stream().filter(it -> it.toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());

            default:
                return values;
        }
    }

    @Override
    public @NotNull Class<?> getType() {
        return String.class;
    }
}
