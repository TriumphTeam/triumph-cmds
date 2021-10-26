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
package dev.triumphteam.cmd.core.flag.internal;

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
public final class FlagGroup<S> {

    private final Map<String, FlagOptions<S>> flags = new HashMap<>();
    private final Map<String, FlagOptions<S>> longFlags = new HashMap<>();
    private final List<String> requiredFlags = new ArrayList<>();

    /**
     * Adds a new flag to the group.
     *
     * @param flagOptions The {@link FlagOptions} that should be added to the lis.
     */
    public void addFlag(@NotNull final FlagOptions<S> flagOptions) {
        final String key = flagOptions.getKey();

        final String longFlag = flagOptions.getLongFlag();
        if (longFlag != null) {
            longFlags.put(longFlag, flagOptions);
        }

        if (flagOptions.isRequired()) {
            requiredFlags.add(flagOptions.getKey());
        }

        flags.put(key, flagOptions);
    }

    /**
     * Checks if the flags are empty.
     *
     * @return Whether the flag lists are empty.
     */
    public boolean isEmpty() {
        return flags.isEmpty() && longFlags.isEmpty();
    }

    /**
     * Gets a list with all the required flags.
     *
     * @return A list of requried flags.
     */
    @NotNull
    public List<String> getRequiredFlags() {
        return requiredFlags;
    }

    /**
     * Gets the flag that matches the current token.
     *
     * @param token    The current token, a flag name or not.
     * @param longFlag Whether it's a long flag or a normal one.
     * @return The flag if found or null if not a valid flag.
     */
    @Nullable
    public FlagOptions<S> getMatchingFlag(@NotNull final String token, final boolean longFlag) {
        final String stripped = stripLeadingHyphens(token);

        if (longFlag) {
            return longFlags.get(stripped);
        }

        return flags.get(stripped);
    }

    /**
     * Strips the hyphens from the token.
     *
     * @param token The flag token.
     * @return The flag token without hyphens.
     */
    @NotNull
    private String stripLeadingHyphens(@NotNull final String token) {
        if (token.startsWith("--")) return token.substring(2);
        if (token.startsWith("-")) return token.substring(1);
        return token;
    }

}
