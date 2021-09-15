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
package dev.triumphteam.cmds.core.flag.internal;

import dev.triumphteam.cmds.core.flag.internal.result.InvalidFlagArgumentResult;
import dev.triumphteam.cmds.core.flag.internal.result.ParseResult;
import dev.triumphteam.cmds.core.flag.internal.result.RequiredArgResult;
import dev.triumphteam.cmds.core.flag.internal.result.RequiredFlagsResult;
import dev.triumphteam.cmds.core.flag.internal.result.SuccessResult;
import dev.triumphteam.cmds.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic flag parser.
 * This class could definitely be improved, will be revised before release.
 *
 * @param <S> The sender type.
 */
public final class FlagParser<S> {

    private final List<String> leftOver = new ArrayList<>();
    private final FlagsResult result = new FlagsResult();

    private final FlagGroup<S> flagGroup;
    private final S sender;
    private final FlagScanner flagScanner;

    private final List<String> missingRequiredFlags;
    private final List<String> requiredFlags;

    // These nullable values won't always be needed, could be reworked.
    private String errorFlag = null;
    private String errorArgumentToken = null;
    @Nullable
    private Class<?> errorArgumentType = null;

    private ParseState parseState = ParseState.SUCCESS;

    public FlagParser(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        this.flagGroup = flagGroup;
        this.sender = sender;
        this.flagScanner = new FlagScanner(args);
        this.missingRequiredFlags = new ArrayList<>(flagGroup.getRequiredFlags());
        this.requiredFlags = flagGroup.getRequiredFlags();
    }

    /**
     * Creates a new FlagParser and parses the current flags with the given arguments.
     *
     * @param flagGroup A {@link FlagGroup} with all the flag data.
     * @param sender    The {@link S} sender.
     * @param args      The arguments the sender typed normally extracted to remove unneeded arguments.
     * @param <S>       The sender type.
     * @return A new {@link ParseResult} based on what happens in the code.
     */
    @NotNull
    public static <S> ParseResult<S> parse(@NotNull final FlagGroup<S> flagGroup, @NotNull final S sender, @NotNull final List<String> args) {
        return new FlagParser<S>(flagGroup, sender, args).parseAndBuild();
    }

    /**
     * Parses and builds a new {@link ParseResult}.
     *
     * @return An implementation of {@link ParseResult}.
     */
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

        if (parseState == ParseState.MISSING_REQUIRED_ARGUMENT) {
            // Should never happen
            if (errorFlag == null || errorArgumentType == null) {
                throw new CommandExecutionException("Error occurred when parsing flags.");
            }
            return new RequiredArgResult<>(errorFlag, errorArgumentType);
        }

        if (parseState == ParseState.INVALID_ARGUMENT) {
            // Should never happen
            if (errorFlag == null || errorArgumentToken == null || errorArgumentType == null) {
                throw new CommandExecutionException("Error occurred when parsing flags.");
            }
            return new InvalidFlagArgumentResult<>(errorArgumentToken, errorFlag, errorArgumentType);
        }

        if (parseState == ParseState.SUCCESS && !missingRequiredFlags.isEmpty()) {
            return new RequiredFlagsResult<>(missingRequiredFlags, requiredFlags);
        }

        return new SuccessResult<>(leftOver, result);
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
            // IMPROVE DRY
            errorFlag = flag.getKey();
            errorArgumentType = flag.getArgumentType();
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

        // IMPROVE DRY
        if (argument == null) {
            if (flag.requiresArg()) {
                errorFlag = flag.getKey();
                errorArgumentToken = argToken;
                errorArgumentType = flag.getArgumentType();
                parseState = ParseState.INVALID_ARGUMENT;
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

        // IMPROVE DRY
        if (flag.requiresArg() && argToken.isEmpty()) {
            errorFlag = flag.getKey();
            errorArgumentType = flag.getArgumentType();
            parseState = ParseState.MISSING_REQUIRED_ARGUMENT;
            return;
        }

        // IMPROVE DRY
        final Object argument = flag.resolveArgument(sender, argToken);
        if (argument == null && flag.requiresArg()) {
            errorFlag = flag.getKey();
            errorArgumentToken = argToken;
            errorArgumentType = flag.getArgumentType();
            parseState = ParseState.INVALID_ARGUMENT;
            return;
        }

        addFlag(flag, argument);
    }

    private void addFlag(@NotNull final CommandFlag<S> flag) {
        missingRequiredFlags.remove(flag.getKey());
        result.addFlag(flag);
    }

    private void addFlag(@NotNull final CommandFlag<S> flag, @Nullable final Object argument) {
        missingRequiredFlags.remove(flag.getKey());
        result.addFlag(flag, argument);
    }

    /**
     * Used only inside to check the current state of the parsing, and whether it should break the loop.
     */
    private enum ParseState {
        SUCCESS,
        MISSING_REQUIRED_ARGUMENT,
        INVALID_ARGUMENT
    }

}
