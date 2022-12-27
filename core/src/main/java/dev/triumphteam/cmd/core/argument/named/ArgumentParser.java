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
package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.internal.FlagGroup;
import dev.triumphteam.cmd.core.argument.internal.FlagOptions;
import dev.triumphteam.cmd.core.argument.internal.ParserScanner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Set;

public final class ArgumentParser<S> {

    private static final int SPACE = ' ';
    private static final int ESCAPE_CHAR = '\\';
    private static final int HYPHEN = '-';

    private static final String LONG = "--";
    private static final String SHORT = "-";
    private static final String ESCAPE = "\\";

    private static final int ARGUMENT_SEPARATOR = ':';
    private static final int FLAG_SEPARATOR = '=';

    private static final Set<Integer> ESCAPABLE_CHARS =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(HYPHEN, ARGUMENT_SEPARATOR, FLAG_SEPARATOR)));

    private final FlagGroup<S> flagGroup;
    private final Map<String, InternalArgument<S, ?>> namedArguments;

    public ArgumentParser(
            final @NotNull FlagGroup<S> flagGroup,
            final @NotNull Map<String, InternalArgument<S, ?>> namedArguments
    ) {
        this.flagGroup = flagGroup;
        this.namedArguments = namedArguments;
    }

    public static Map<String, String> parse(final @NotNull String literal) {
        final PrimitiveIterator.OfInt iterator = literal.chars().iterator();

        final Map<String, String> args = new LinkedHashMap<>();
        final StringBuilder builder = new StringBuilder();

        // Control variables
        boolean escape = false;
        String argument = "";

        while (iterator.hasNext()) {
            final int current = iterator.next();

            // Marks next character to be escaped
            if (current == ESCAPE_CHAR && !argument.isEmpty()) {
                escape = true;
                continue;
            }

            // Found a separator
            if (current == ARGUMENT_SEPARATOR && argument.isEmpty()) {
                argument = builder.toString();
                builder.setLength(0);
                continue;
            }

            // Handling for spaces
            // TODO: Scape
            if (current == SPACE) {
                // If no argument is found, discard values
                if (argument.isEmpty()) {
                    builder.setLength(0);
                    continue;
                }

                // If argument is found, accept as value
                args.put(argument, builder.toString());
                builder.setLength(0);
                argument = "";
                continue;
            }

            // If no escapable token was found, aka :, re-append the backslash
            if (escape) {
                builder.appendCodePoint(ESCAPE_CHAR);
                escape = false;
            }

            // Normal append character
            builder.appendCodePoint(current);
        }

        // If end of string is reached and value was not closed, close it
        if (!argument.isEmpty()) args.put(argument, builder.toString());

        return args;
    }

    public void newParse(final @NotNull List<String> arguments) {
        final ParserScanner tokens = new ParserScanner(arguments);

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

                // Splits the flag from `name:arg`
                final String namedToken = token.substring(0, separator);
                final String argToken = token.substring(separator + 1);

                final InternalArgument<S, ?> internalArgument = namedArguments.get(namedToken);
                // If there is no valid argument we ignore it
                if (internalArgument == null) {
                    result.addNonToken(token);
                    continue;
                }

                result.addNamedArgument(namedToken, argToken);
                result.setWaitingArgument(argToken.isEmpty());
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

        result.test();
    }

    private void handleNoEquals(
            final @NotNull ParserScanner tokens,
            final @NotNull Result result,
            final @NotNull String token

    ) {
        final FlagOptions<S> flag = flagGroup.getMatchingFlag(token);
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

    private void handleWithEquals(
            final @NotNull Result result,
            final @NotNull String token,
            final int equals
    ) {
        // Splits the flag from `flag=arg`
        final String flagToken = token.substring(0, equals);
        final String argToken = token.substring(equals + 1);

        final FlagOptions<S> flag = flagGroup.getMatchingFlag(flagToken);
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

    public class Result {

        private final Map<FlagOptions<S>, String> flags = new HashMap<>();
        private final Map<String, String> namedArguments = new HashMap<>();
        private final List<String> nonTokens = new ArrayList<>();

        private boolean waitingArgument = false;

        public void addNamedArgument(final @NotNull String name) {
            namedArguments.put(name, "");
        }

        public void addNamedArgument(final @NotNull String name, final @NotNull String value) {
            namedArguments.put(name, value);
        }

        public void addFlag(final @NotNull FlagOptions<S> flagOptions) {
            flags.put(flagOptions, "");
        }

        public void addFlag(final @NotNull FlagOptions<S> flagOptions, final @NotNull String value) {
            flags.put(flagOptions, value);
        }

        public void addNonToken(final @NotNull String token) {
            nonTokens.add(token);
        }

        public void setWaitingArgument(final boolean value) {
            this.waitingArgument = value;
        }

        public void test() {
            System.out.println(flags);
            System.out.println(namedArguments);
            System.out.println(nonTokens);
            System.out.println(waitingArgument);
        }
    }
}
