/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.core.command.flag.internal;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public final class FlagParser<S> {

    private final List<String> leftOver = new LinkedList<>();
    private final FlagsResult result = new FlagsResult();

    private final FlagGroup<S> flagGroup;
    private final S sender;
    private final Scanner scanner;
    private boolean fail = false;

    public FlagParser(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        this.flagGroup = flagGroup;
        this.sender = sender;
        this.scanner = new Scanner(args);
    }

    @NotNull
    public static <S> ParseResult parse(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        return new FlagParser<S>(flagGroup, sender, args).parseAndBuild();
    }

    @NotNull
    public ParseResult parseAndBuild() {
        while (scanner.hasNext()) {
            if (fail) break;

            scanner.next();
            final String token = scanner.peek();

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

        //if (fail) return null;
        return new ParseResult(leftOver, result);
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

        if (flag.requiresArg() && !scanner.hasNext()) {
            fail = true;
            return;
        }

        if (!flag.hasArgument()) {
            result.addFlag(flag);
            return;
        }

        scanner.next();
        final String argToken = scanner.peek();
        final Object argument = flag.resolveArgument(sender, argToken);

        if (argument == null) {
            if (flag.requiresArg()) {
                fail = true;
                return;
            }

            scanner.previous();
        }

        result.addFlag(flag, argument);
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
            fail = true;
            return;
        }

        final Object argument = flag.resolveArgument(sender, argToken);

        if (argument == null && flag.requiresArg()) {
            fail = true;
            return;
        }

        result.addFlag(flag, argument);
    }

}
