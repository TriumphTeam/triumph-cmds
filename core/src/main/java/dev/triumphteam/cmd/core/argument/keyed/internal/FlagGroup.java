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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basically a holder that contains all the needed flags for the command.
 *
 * @param <S> The sender type.
 */
final class FlagGroup<S> implements ArgumentGroup<Flag> {

    private final Map<String, Flag> flags = new HashMap<>();
    private final Map<String, Flag> longFlags = new HashMap<>();

    private final List<String> allFlags = new ArrayList<>();

    @Override
    public void addArgument(final @NotNull Flag argument) {
        final String key = argument.getKey();

        final String longFlag = argument.getLongFlag();
        if (longFlag != null) {
            allFlags.add("--" + longFlag);
            longFlags.put(longFlag, argument);
        }

        allFlags.add("-" + key);
        flags.put(key, argument);
    }

    @Override
    public @NotNull List<String> getAllNames() {
        return allFlags;
    }

    @Override
    public boolean isEmpty() {
        return flags.isEmpty() && longFlags.isEmpty();
    }

    @Override
    public @Nullable Flag getMatchingArgument(final @NotNull String token) {
        final String stripped = stripLeadingHyphens(token);

        final Flag flag = flags.get(stripped);
        return flag != null ? flag : longFlags.get(stripped);
    }

    /**
     * Strips the hyphens from the token.
     *
     * @param token The flag token.
     * @return The flag token without hyphens.
     */
    private @NotNull String stripLeadingHyphens(final @NotNull String token) {
        if (token.startsWith("--")) return token.substring(2);
        if (token.startsWith("-")) return token.substring(1);
        return token;
    }
}
