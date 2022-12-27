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

import com.google.common.collect.Maps;
import dev.triumphteam.cmd.core.flag.Flags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic flag parser.
 *
 * @param <S> The sender type.
 */
public final class FlagParser<S> {

    private final FlagGroup<S> flagGroup;

    private static final String ESCAPE = "\\";
    private static final String LONG = "--";
    private static final String SHORT = "-";
    private static final int EQUALS = '=';

    public FlagParser(final @NotNull FlagGroup<S> flagGroup) {
        this.flagGroup = flagGroup;
    }

    public @NotNull Map<@NotNull FlagOptions<S>, @NotNull String> parseFlags(final @NotNull List<@NotNull String> toParse) {
        return parseInternal(toParse).getKey();
    }

    public @NotNull Flags parse(final @NotNull S sender, final @NotNull List<@NotNull String> toParse) {
        final Map.Entry<Map<FlagOptions<S>, String>, List<String>> parsed = parseInternal(toParse);
        return new FlagsResult<>(sender, parsed.getKey(), parsed.getValue());
    }

    private Map.@NotNull Entry<@NotNull Map<@NotNull FlagOptions<S>, @NotNull String>, @NotNull List<@NotNull String>> parseInternal(final @NotNull List<@NotNull String> toParse) {
        final FlagScanner tokens = new FlagScanner(toParse);

        final Map<FlagOptions<S>, String> flags = new LinkedHashMap<>();
        final List<String> args = new ArrayList<>();

        while (tokens.hasNext()) {
            final String token = tokens.next();

            // If escaping the flag then just, skip
            if (token.startsWith(ESCAPE)) {
                args.add(token);
                continue;
            }

            // Checks if it's a flag, if not then skip
            if ((!token.startsWith(LONG) || LONG.equals(token)) && (!token.startsWith(SHORT) || SHORT.equals(token))) {
                args.add(token);
                continue;
            }

            final int equals = token.indexOf(EQUALS);
            // No equals char was found
            if (equals == -1) {
                final FlagOptions<S> flag = flagGroup.getMatchingFlag(token);
                // No valid flag with the name, skip
                if (flag == null) {
                    args.add(token);
                    continue;
                }

                // Checks if the flag needs argument
                if (flag.hasArgument()) {
                    // If an argument is needed and no more tokens present, then just append empty as value
                    if (!tokens.hasNext()) {
                        flags.put(flag, "");
                        continue;
                    }

                    // Value found so append
                    flags.put(flag, tokens.next());
                    continue;
                }

                // No argument needed just add flag
                flags.put(flag, null);
                continue;
            }

            // Splits the flag from `flag=arg`
            final String flagToken = token.substring(0, equals);
            final String argToken = token.substring(equals + 1);

            final FlagOptions<S> flag = flagGroup.getMatchingFlag(flagToken);
            // No valid flag with the name, skip
            if (flag == null) {
                args.add(token);
                continue;
            }

            // Flag with equals should always have argument, so we ignore if it doesn't
            if (!flag.hasArgument()) {
                args.add(token);
                continue;
            }

            // Add flag normally
            flags.put(flag, argToken);
        }

        return Maps.immutableEntry(flags, args);
    }
}
