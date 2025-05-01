package dev.triumphteam.cmd.core.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SuggestionContext<S> {

    private final String input;
    private final S sender;
    private final List<String> arguments;

    public SuggestionContext(
            final @NotNull String input,
            final @NotNull S sender,
            final @NotNull List<String> arguments
    ) {
        this.input = input;
        this.sender = sender;
        this.arguments = arguments;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SuggestionContext<?> that = (SuggestionContext<?>) o;
        return input.equals(that.input) && sender.equals(that.sender) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * input.hashCode() + sender.hashCode()) + arguments.hashCode();
    }

    @Override
    public @NotNull String toString() {
        return "SuggestionContext{" +
                "input='" + input + '\'' +
                ", sender=" + sender +
                ", arguments=" + arguments +
                '}';
    }
}
