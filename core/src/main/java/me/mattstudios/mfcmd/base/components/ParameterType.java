package me.mattstudios.mfcmd.base.components;

public final class ParameterType {

    private final Class<?> type;
    private final String completion;

    public ParameterType(final Class<?> type, final String completion) {
        this.type = type;
        this.completion = completion;
    }

    public Class<?> getType() {
        return type;
    }

    public String getCompletion() {
        return completion;
    }

}
