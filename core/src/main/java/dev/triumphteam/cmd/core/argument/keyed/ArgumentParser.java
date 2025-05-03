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

import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class ArgumentParser {

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
    public Result parse(final @NotNull Collection<String> arguments) {
        final Iterator<String> tokens = arguments.iterator();

        final Result result = new Result();

        boolean pendingResultReset = false;

        while (tokens.hasNext()) {
            final String token = tokens.next();

            // Reset waiting argument because it's a new token
            result.setArgumentWaiting(null);
            result.setCurrent(token);

            // Reset the flag argument that is pending
            if (pendingResultReset) {
                pendingResultReset = false;
                result.setFlagWaiting(null);
            }

            // If escaping the flag, then skip
            if (token.startsWith(ESCAPE)) {
                result.addNonToken(token);
                continue;
            }

            final Pair<Flag, Result.FlagType> waitingFlag = result.getFlagWaiting();
            if (waitingFlag != null) {
                // Threat token as an argument
                result.addFlag(waitingFlag.first(), token);
                result.setCurrent(token);

                // Mark for a result reset after
                pendingResultReset = true;
                continue;
            }

            // Checks if it's a flag, if not, then it could be named
            if ((!token.startsWith(LONG) || LONG.equals(token)) && (!token.startsWith(SHORT) || SHORT.equals(token))) {
                final int separator = token.indexOf(ARGUMENT_SEPARATOR);

                // Not a flag nor a named argument, so just ignore
                if (separator == -1) {
                    final Argument partial = namedGroup.matchPartialSingle(token);
                    if (partial != null) {
                        result.setArgumentWaiting(partial);
                    }

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
                handleNoEquals(result, token);
                continue;
            }

            // Handling of arguments with equals
            handleWithEquals(result, token, equals);
            pendingResultReset = true;
        }

        return result;
    }

    /**
     * Parser handler for named arguments.
     *
     * @param result    The result instance to add to.
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

        final Argument argument = namedGroup.matchExact(namedToken);
        // If there is no valid argument we ignore it
        if (argument == null) {
            result.addNonToken(token);
            return;
        }

        result.addNamedArgument(argument, argToken);
        result.setCurrent(argToken);
        result.setArgumentWaiting(argument);
    }

    /**
     * Parser handler for flags without an equals.
     * The argument would be the next iteration.
     *
     * @param result The results instance to add to.
     * @param token  The current flag token.
     */
    private void handleNoEquals(
            final @NotNull Result result,
            final @NotNull String token

    ) {
        final Flag flag = flagGroup.matchExact(token);
        // No valid flag with the name, skip
        if (flag == null) {
            result.addNonToken(token);
            return;
        }

        // Checks if the flag needs argument
        if (flag.hasArgument()) {
            // Waiting with a type
            final Result.FlagType type = token.startsWith("--") ? Result.FlagType.LONG_NO_EQUALS : Result.FlagType.FLAG_NO_EQUALS;
            result.setFlagWaiting(new Pair<>(flag, type));
            result.setCurrent(token);
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

        final Flag flag = flagGroup.matchExact(flagToken);
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
        result.setCurrent(argToken);
        // Waiting with a type
        final Result.FlagType type = token.startsWith("--") ? Result.FlagType.LONG : Result.FlagType.FLAG;
        result.setFlagWaiting(new Pair<>(flag, type), true);
    }

    public static class Result {

        private final Map<Flag, String> flags = new HashMap<>();
        private final Map<Argument, String> namedArguments = new HashMap<>();
        private final List<String> nonTokens = new ArrayList<>();

        private String current = "";
        private Argument argumentWaiting = null;
        private Pair<Flag, FlagType> flagWaiting = null;

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

        public @Nullable Argument getArgumentWaiting() {
            return argumentWaiting;
        }

        public void setArgumentWaiting(final @Nullable Argument argumentWaiting) {
            this.argumentWaiting = argumentWaiting;
        }

        public @Nullable Pair<Flag, FlagType> getFlagWaiting() {
            return flagWaiting;
        }

        public void setFlagWaiting(final @Nullable Pair<Flag, FlagType> flagWaiting) {
            setFlagWaiting(flagWaiting, false);
        }

        public void setFlagWaiting(final @Nullable Pair<Flag, FlagType> flagWaiting, final boolean ignore) {
            if (!ignore && (flagWaiting != null && flags.containsKey(flagWaiting.first()))) {
                return;
            }
            this.flagWaiting = flagWaiting;
        }

        public @NotNull String getCurrent() {
            return current;
        }

        public void setCurrent(final @NotNull String current) {
            this.current = current;
        }

        public enum FlagType {
            FLAG,
            FLAG_NO_EQUALS,

            LONG,
            LONG_NO_EQUALS;

            public boolean isLong() {
                return this == LONG || this == LONG_NO_EQUALS;
            }

            public boolean hasEquals() {
                return this == FLAG || this == LONG;
            }
        }
    }
}
