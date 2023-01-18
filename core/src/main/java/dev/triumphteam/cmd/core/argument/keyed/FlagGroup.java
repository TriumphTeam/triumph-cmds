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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basically a holder that contains all the needed flags for the command.
 */
final class FlagGroup implements ArgumentGroup<Flag> {

    private final Map<String, Flag> flags = new HashMap<>();
    private final Map<String, Flag> longFlags = new HashMap<>();

    private final Map<String, Flag> allFlags = new HashMap<>();

    public FlagGroup(final @NotNull List<Flag> flags) {
        flags.forEach(this::addArgument);
    }

    public void addArgument(final @NotNull Flag argument) {
        final String key = argument.getKey();

        final String longFlag = argument.getLongFlag();
        if (longFlag != null) {
            allFlags.put("--" + longFlag, argument);
            longFlags.put(longFlag, argument);
        }

        allFlags.put("-" + key, argument);
        flags.put(key, argument);
    }

    @Override
    public @NotNull Set<String> getAllNames() {
        return allFlags.keySet();
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

    @Override
    public @NotNull Set<Flag> getAll() {
        return new HashSet<>(flags.values());
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

    @Override
    public String toString() {
        return "FlagGroup{" +
                "allFlags=" + allFlags +
                '}';
    }
}
