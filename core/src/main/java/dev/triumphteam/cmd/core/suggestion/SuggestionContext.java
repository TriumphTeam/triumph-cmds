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
package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class SuggestionContext<S> {

    private final String input;
    private final S sender;
    private final List<String> arguments;
    private final String extra;

    public SuggestionContext(
            final @NotNull String input,
            final @NotNull S sender,
            final @NotNull List<String> arguments,
            final @NotNull String extra
    ) {
        this.input = input;
        this.sender = sender;
        this.arguments = arguments;
        this.extra = extra;
    }

    public static <S> @NotNull SuggestionContext<S> of(
            final @NotNull String input,
            final @NotNull S sender,
            final @NotNull List<String> arguments,
            final @NotNull String extra
    ) {
        return new SuggestionContext<>(input, sender, arguments, extra);
    }

    public static <S> @NotNull SuggestionContext<S> of(
            final @NotNull String input,
            final @NotNull S sender,
            final @NotNull List<String> arguments
    ) {
        return new SuggestionContext<>(input, sender, arguments, "");
    }

    public @NotNull String getInput() {
        return input;
    }

    public @NotNull S getSender() {
        return sender;
    }

    public @NotNull List<String> getArguments() {
        return arguments;
    }

    public @NotNull String getExtra() {
        return extra;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final SuggestionContext<?> that = (SuggestionContext<?>) o;
        return Objects.equals(input, that.input) && Objects.equals(sender, that.sender) && Objects.equals(arguments, that.arguments) && Objects.equals(extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, sender, arguments, extra);
    }

    @Override
    public @NotNull String toString() {
        return "SuggestionContext{" +
                "input='" + input + '\'' +
                ", sender=" + sender +
                ", arguments=" + arguments +
                ", extra='" + extra + '\'' +
                '}';
    }
}
