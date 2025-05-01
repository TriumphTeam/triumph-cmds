package dev.triumphteam.cmd.core.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ArgumentInput {

    private final String input;
    private final Object provided;

    public ArgumentInput(final @NotNull String input) {
        this(input, null);
    }

    public ArgumentInput(final @NotNull String input, final @Nullable Object provided) {
        this.input = input;
        this.provided = provided;
    }

    public @NotNull String getInput() {
        return input;
    }

    public @Nullable Object getProvided() {
        return provided;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ArgumentInput that = (ArgumentInput) o;
        return Objects.equals(input, that.input) && Objects.equals(provided, that.provided);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, provided);
    }

    @Override
    public String toString() {
        return "SuppliedArgument{" +
                "input='" + input + '\'' +
                ", provided=" + provided +
                '}';
    }
}
