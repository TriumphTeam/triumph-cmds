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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ArgumentParser {

    private static final String LONG = "--";
    private static final String SHORT = "-";
    private static final String ESCAPE = "\\";

    private static final int ARGUMENT_SEPARATOR = ':';
    private static final int FLAG_SEPARATOR = '=';

    private final ArgumentGroup<Flag> flagGroup;
    private final ArgumentGroup<Argument> namedGroup;

    public ArgumentParser(
            final @NotNull ArgumentGroup<Flag> flagGroup,
            final @NotNull ArgumentGroup<Argument> namedGroup
    ) {
        this.flagGroup = flagGroup;
        this.namedGroup = namedGroup;
    }

    /**
     * Parse the current {@link List} of raw arguments for {@link Flag}s and {@link Argument}.
     *
     * @param arguments A {@link List} of raw arguments.
     * @return A {@link Result} object containing the raw results of the parse.
     */
    public Result parse(final @NotNull List<String> arguments) {
        final Iterator<String> tokens = arguments.iterator();

        final Result result = new Result();

        while (tokens.hasNext()) {
            final String token = tokens.next();

            // If escaping the flag then just, skip
            if (token.startsWith(ESCAPE)) {
                result.addNonToken(token);
                continue;
            }

            // Checks if it's a flag, if not then skip
            if ((!token.startsWith(LONG) || LONG.equals(token)) && (!token.startsWith(SHORT) || SHORT.equals(token))) {
                final int separator = token.indexOf(ARGUMENT_SEPARATOR);

                // Not a flag nor a named argument, so just ignore
                if (separator == -1) {
                    result.addNonToken(token);
                    continue;
                }

                // Handling of named arguments
                handleNamed(result, token, separator);
                continue;
            }

            final int equals = token.indexOf(FLAG_SEPARATOR);
            // No equals char was found
            if (equals == -1) {
                handleNoEquals(tokens, result, token);
                continue;
            }

            // Handling of arguments with equals
            handleWithEquals(result, token, equals);
        }

        return result;
    }

    /**
     * Parser handler for named arguments.
     *
     * @param result    The results instance to add to.
     * @param token     The current named argument token.
     * @param separator The position of the separator.
     */
    private void handleNamed(
            final @NotNull Result result,
            final @NotNull String token,
            final int separator
    ) {
        // Splits the flag from `name:arg`
        final String namedToken = token.substring(0, separator);
        final String argToken = token.substring(separator + 1);

        final Argument argument = namedGroup.getMatchingArgument(namedToken);
        // If there is no valid argument we ignore it
        if (argument == null) {
            result.addNonToken(token);
            return;
        }

        result.addNamedArgument(argument, argToken);
        result.setWaitingArgument(argToken.isEmpty());
    }

    /**
     * Parser handler for flags without an equals.
     * The argument would be the next iteration.
     *
     * @param tokens The tokens {@link Iterator} from the loop.
     * @param result The results instance to add to.
     * @param token  The current flag token.
     */
    private void handleNoEquals(
            final @NotNull Iterator<String> tokens,
            final @NotNull Result result,
            final @NotNull String token

    ) {
        final Flag flag = flagGroup.getMatchingArgument(token);
        // No valid flag with the name, skip
        if (flag == null) {
            result.addNonToken(token);
            return;
        }

        // Checks if the flag needs argument
        if (flag.hasArgument()) {
            // If an argument is needed and no more tokens present, then just append empty as value
            if (!tokens.hasNext()) {
                result.addFlag(flag);
                result.setWaitingArgument(true);
                return;
            }

            // Value found so append
            result.addFlag(flag, tokens.next());
            result.setWaitingArgument(false);
            return;
        }

        // No argument needed just add flag
        result.addFlag(flag);
    }

    /**
     * Parser handler for flags with an equals.
     *
     * @param result The results instance to add to.
     * @param token  The current flag token.
     */
    private void handleWithEquals(
            final @NotNull Result result,
            final @NotNull String token,
            final int equals
    ) {
        // Splits the flag from `flag=arg`
        final String flagToken = token.substring(0, equals);
        final String argToken = token.substring(equals + 1);

        final Flag flag = flagGroup.getMatchingArgument(flagToken);
        // No valid flag with the name, skip
        if (flag == null) {
            result.addNonToken(token);
            return;
        }

        // Flag with equals should always have argument, so we ignore if it doesn't
        if (!flag.hasArgument()) {
            result.addNonToken(token);
            return;
        }

        // Add flag normally
        result.addFlag(flag, argToken);
        result.setWaitingArgument(argToken.isEmpty());
    }

    public static class Result {

        private final Map<Flag, String> flags = new HashMap<>();
        private final Map<Argument, String> namedArguments = new HashMap<>();
        private final List<String> nonTokens = new ArrayList<>();

        private boolean waitingArgument = false;

        public void addNamedArgument(final @NotNull Argument argument, final @NotNull String value) {
            namedArguments.put(argument, value);
        }

        public void addFlag(final @NotNull Flag flagOptions) {
            flags.put(flagOptions, "");
        }

        public void addFlag(final @NotNull Flag flagOptions, final @NotNull String value) {
            flags.put(flagOptions, value);
        }

        public void addNonToken(final @NotNull String token) {
            nonTokens.add(token);
        }

        public Map<Flag, String> getFlags() {
            return flags;
        }

        public Map<Argument, String> getNamedArguments() {
            return namedArguments;
        }

        public List<String> getNonTokens() {
            return nonTokens;
        }

        public boolean isWaitingArgument() {
            return waitingArgument;
        }

        public void setWaitingArgument(final boolean value) {
            this.waitingArgument = value;
        }
    }
}
