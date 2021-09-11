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
package dev.triumphteam.cmds.core.command.flag.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class FlagGroup<S> {

    private final Map<String, CommandFlag<S>> flags = new LinkedHashMap<>();
    private final Map<String, CommandFlag<S>> longFlags = new LinkedHashMap<>();
    private final Set<CommandFlag<S>> requiredFlags = new HashSet<>();

    public void addFlag(@NotNull final CommandFlag<S> commandFlag) {
        final String key = commandFlag.getKey();

        final String longFlag = commandFlag.getLongFlag();
        if (longFlag != null) {
            longFlags.put(longFlag, commandFlag);
        }

        if (commandFlag.isRequired()) {
            requiredFlags.add(commandFlag);
        }

        flags.put(key, commandFlag);
    }

    public boolean isEmpty() {
        return flags.isEmpty() && longFlags.isEmpty();
    }

    @NotNull
    public Set<CommandFlag<S>> getRequiredFlags() {
        return requiredFlags;
    }

    @Nullable
    public CommandFlag<S> getMatchingFlag(@NotNull final String token, final boolean longFlag) {
        final String stripped = stripLeadingHyphens(token);

        if (longFlag) {
            return longFlags.get(stripped);
        }

        return flags.get(stripped);
    }

    private String stripLeadingHyphens(@NotNull final String str) {
        if (str.startsWith("--")) return str.substring(2);
        if (str.startsWith("-")) return str.substring(1);
        return str;
    }

}
