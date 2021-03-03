package dev.triumphteam.core.internal;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractManager {

    public void registerCommand(@NotNull final CommandBase... commands) {
        for (final CommandBase command : commands) {
            registerCommand(command);
        }
    }

    public abstract void registerCommand(@NotNull final CommandBase command);

    protected void registerCommand() {
        
    }

}
