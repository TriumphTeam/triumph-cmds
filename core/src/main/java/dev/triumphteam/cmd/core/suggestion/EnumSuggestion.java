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
import dev.triumphteam.cmd.core.util.EnumUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EnumSuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    private final Class<? extends Enum<?>> enumType;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;
    private final boolean suggestLowercase;

    public EnumSuggestion(
            final @NotNull Class<? extends Enum<?>> enumType,
            final @NotNull SuggestionMapper<ST> mapper,
            final @NotNull SuggestionMethod method,
            final boolean suggestLowercase
    ) {
        this.enumType = enumType;
        this.mapper = mapper;
        this.method = method;
        this.suggestLowercase = suggestLowercase;

        EnumUtils.populateCache(enumType);
    }

    @Override
    public @NotNull List<ST> getSuggestions(
            final @NotNull S sender,
            final @NotNull String current,
            final @NotNull List<String> arguments,
            final @NotNull Map<String, String> argumentsMap
    ) {
        final List<String> suggestions = EnumUtils.getEnumConstants(enumType)
                .values()
                .stream()
                .map(it -> {
                    final Enum<?> constant = it.get();
                    if (constant == null) return null;
                    final String name = constant.name();
                    return suggestLowercase ? name.toLowerCase() : name;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return mapper.filter(current, mapper.map(suggestions), method);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final EnumSuggestion<?, ?> that = (EnumSuggestion<?, ?>) o;
        return suggestLowercase == that.suggestLowercase && Objects.equals(enumType, that.enumType) && Objects.equals(mapper, that.mapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumType, suggestLowercase, mapper);
    }

    @Override
    public @NotNull String toString() {
        return "EnumSuggestion{" +
                "enumType=" + enumType +
                ", suggestLowercase=" + suggestLowercase +
                ", mapper=" + mapper +
                '}';
    }
}
