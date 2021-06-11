package dev.triumphteam.core.argument;

public final class BasicArgument implements Argument {

    private final Class<?> type;

    public BasicArgument(final Class<?> type) {
        this.type = type;
    }

    @Override
    public Object resolve(final Object value) {

        return "resolved";
    }
}
