package me.mattstudios.mf.base.components;

public final class TypeResult {

    private Object resolvedValue;
    private String argumentName;

    /**
     * Main constructor
     *
     * @param resolvedValue The resolved value
     * @param argumentName  The argument
     */
    public TypeResult(final Object resolvedValue, final Object argumentName) {
        this.resolvedValue = resolvedValue;
        this.argumentName = String.valueOf(argumentName);
    }

    /**
     * Secondary constructor with just the argument name
     *
     * @param argumentName The argument
     */
    public TypeResult(final Object argumentName) {
        this(null, argumentName);
    }

    /**
     * Gets the resolved value
     *
     * @return The resolved value
     */
    public Object getResolvedValue() {
        return resolvedValue;
    }

    /**
     * Gets the argument name
     *
     * @return The argument name
     */
    public String getArgumentName() {
        return argumentName;
    }
}
