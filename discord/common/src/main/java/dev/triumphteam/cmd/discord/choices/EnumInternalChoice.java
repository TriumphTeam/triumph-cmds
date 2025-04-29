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
package dev.triumphteam.cmd.discord.choices;

import dev.triumphteam.cmd.core.util.EnumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EnumInternalChoice implements InternalChoice {

    private final Class<? extends Enum<?>> enumType;

    public EnumInternalChoice(final @NotNull Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
        EnumUtils.populateCache(enumType);
    }

    @Override
    public @NotNull List<String> getChoices() {
        return EnumUtils.getEnumConstants(enumType)
                .values()
                .stream()
                .map(it -> {
                    final Enum<?> constant = it.get();
                    if (constant == null) return null;
                    return constant.name();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EnumInternalChoice that = (EnumInternalChoice) o;
        return enumType.equals(that.enumType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumType);
    }

    @Override
    public @NotNull String toString() {
        return "EnumChoice{" +
                "enumType=" + enumType +
                '}';
    }
}
