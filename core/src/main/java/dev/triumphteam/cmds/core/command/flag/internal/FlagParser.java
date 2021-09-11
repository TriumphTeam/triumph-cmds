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

import java.util.ArrayList;
import java.util.List;

public final class FlagParser<S> {

    private final List<String> leftOver = new ArrayList<>();
    private final FlagsResult result = new FlagsResult();

    private final FlagGroup<S> flagGroup;
    private final S sender;
    private final FlagScanner flagScanner;
    private final List<CommandFlag<S>> requiredFlags;
    private ParseState parseState = ParseState.SUCCESS;

    public FlagParser(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        this.flagGroup = flagGroup;
        this.sender = sender;
        this.flagScanner = new FlagScanner(args);
        requiredFlags = new ArrayList<>(flagGroup.getRequiredFlags());
    }

    @NotNull
    public static <S> ParseResult<S> parse(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        return new FlagParser<S>(flagGroup, sender, args).parseAndBuild();
    }

    @NotNull
    public ParseResult<S> parseAndBuild() {
        while (flagScanner.hasNext()) {
            if (parseState != ParseState.SUCCESS) break;

            flagScanner.next();
            final String token = flagScanner.peek();

            if (token.startsWith("--") && !"--".equals(token)) {
                handleFlag(token, true);
                continue;
            }

            if (token.startsWith("-") && !"-".equals(token)) {
                handleFlag(token, false);
                continue;
            }

            leftOver.add(token);
        }

        if (parseState == ParseState.SUCCESS && !requiredFlags.isEmpty()) {
            parseState = ParseState.MISSING_REQUIRED_FLAG;
        }

        return new ParseResult<>(leftOver, result, parseState, requiredFlags);
    }

    private void handleFlag(@NotNull final String token, final boolean longFlag) {
        int equalsIndex = token.indexOf('=');
        if (equalsIndex == -1) {
            handleFlagWithoutEquals(token, longFlag);
            return;
        }

        handleFlagWithEquals(token, equalsIndex, longFlag);
    }

    private void handleFlagWithoutEquals(@NotNull final String token, final boolean longFlag) {
        final CommandFlag<S> flag = flagGroup.getMatchingFlag(token, longFlag);
        if (flag == null) {
            leftOver.add(token);
            return;
        }

        if (flag.requiresArg() && !flagScanner.hasNext()) {
            parseState = ParseState.MISSING_REQUIRED_ARGUMENT;
            return;
        }

        if (!flag.hasArgument()) {
            addFlag(flag);
            return;
        }

        flagScanner.next();
        final String argToken = flagScanner.peek();
        final Object argument = flag.resolveArgument(sender, argToken);

        if (argument == null) {
            if (flag.requiresArg()) {
                parseState = ParseState.MISSING_REQUIRED_ARGUMENT;
                return;
            }

            flagScanner.previous();
        }

        addFlag(flag, argument);
    }

    private void handleFlagWithEquals(@NotNull final String token, final int equalsIndex, final boolean longFlag) {
        final String argToken = token.substring(equalsIndex + 1);
        final String flagToken = token.substring(0, equalsIndex);
        final CommandFlag<S> flag = flagGroup.getMatchingFlag(flagToken, longFlag);
        if (flag == null) {
            leftOver.add(token);
            return;
        }

        if (flag.requiresArg() && argToken.isEmpty()) {
            parseState = ParseState.MISSING_REQUIRED_ARGUMENT;
            return;
        }

        final Object argument = flag.resolveArgument(sender, argToken);

        if (argument == null && flag.requiresArg()) {
            parseState = ParseState.INVALID_ARGUMENT;
            return;
        }

        addFlag(flag, argument);
    }

    private void addFlag(@NotNull final CommandFlag<S> flag) {
        requiredFlags.remove(flag.getKey());
        result.addFlag(flag);
    }

    private void addFlag(@NotNull final CommandFlag<S> flag, @Nullable final Object argument) {
        requiredFlags.remove(flag.getKey());
        result.addFlag(flag, argument);
    }

    enum ParseState {
        SUCCESS,
        MISSING_REQUIRED_ARGUMENT,
        MISSING_REQUIRED_FLAG,
        INVALID_ARGUMENT
    }

}
