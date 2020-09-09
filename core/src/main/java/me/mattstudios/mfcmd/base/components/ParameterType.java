package me.mattstudios.mfcmd.base.components;

public final class ParameterType {

    private final Class<?> type;

    private String completion;
    private boolean value;

    public ParameterType(final Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(final String completion) {
        this.completion = completion;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[Parameter = " + type.getName() + " - Completion = " + completion + "]";
    }

}
