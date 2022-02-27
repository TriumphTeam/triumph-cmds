package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class NamedArgumentResult implements Arguments {

    private final Map<String, Object> values;

    public NamedArgumentResult(@NotNull final Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "values=" + values +
                '}';
    }
}
