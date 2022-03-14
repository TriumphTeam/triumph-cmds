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
package dev.triumphteam.cmd.core.flag.internal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public FlagParser(@NotNull final FlagGroup<S> flagGroup) {
        this.flagGroup = flagGroup;
    }

    public FlagsResult<S> parse(@NotNull final S sender, @NotNull final List<String> toParse) {
        final FlagScanner tokens = new FlagScanner(toParse);

        final FlagsResult<S> flagsResult = new FlagsResult<>(sender);

        while (tokens.hasNext()) {
            final String token = tokens.next();

            // If escaping the flag then just, skip
            if (token.startsWith(ESCAPE)) {
                flagsResult.addArg(token);
                continue;
            }

            // Checks if it's a flag, if not then skip
            if ((!token.startsWith(LONG) || LONG.equals(token)) && (!token.startsWith(SHORT) || SHORT.equals(token))) {
                flagsResult.addArg(token);
                continue;
            }

            final int equals = token.indexOf(EQUALS);
            // No equals char was found
            if (equals == -1) {
                final FlagOptions<S> flag = flagGroup.getMatchingFlag(token);
                // No valid flag with the name, skip
                if (flag == null) {
                    flagsResult.addArg(token);
                    continue;
                }

                // Checks if the flag needs argument
                if (flag.hasArgument()) {
                    // If an argument is needed and no more tokens present, then just append empty as value
                    if (!tokens.hasNext()) {
                        flagsResult.addFlag(flag, "");
                        continue;
                    }

                    // Value found so append
                    flagsResult.addFlag(flag, tokens.next());
                    continue;
                }

                // No argument needed just add flag
                flagsResult.addFlag(flag);
                continue;
            }

            // Splits the flag from `flag=arg`
            final String flagToken = token.substring(0, equals);
            final String argToken = token.substring(equals + 1);

            final FlagOptions<S> flag = flagGroup.getMatchingFlag(flagToken);
            // No valid flag with the name, skip
            if (flag == null) {
                flagsResult.addArg(token);
                continue;
            }

            // Flag with equals should always have argument, so we ignore if it doesn't
            if (!flag.hasArgument()) {
                flagsResult.addArg(token);
                continue;
            }

            // Add flag normally
            flagsResult.addFlag(flag, argToken);
        }

        return flagsResult;
    }
}
