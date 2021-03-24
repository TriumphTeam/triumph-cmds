package dev.triumphteam.core.internal;

public abstract class SubCommand {

    private final boolean isDefault;

    public SubCommand(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }
    
}
