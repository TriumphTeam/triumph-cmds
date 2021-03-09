package dev.triumphteam.core.internal;

import dev.triumphteam.core.internal.registry.ArgumentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base command manager for all platforms
 */
public abstract class CommandManager {

    @NotNull
    private Map<String, CoreCommand> commands = new LinkedHashMap<>();

    @NotNull
    private final ArgumentRegistry argumentRegistry = new ArgumentRegistry();

    /**
     * Main registering method to be implemented in other platform command managers
     *
     * @param command The {@link CommandBase} to be registered
     */
    public abstract void registerCommand(@NotNull final CommandBase command);

    /**
     * Method to register commands with vararg
     *
     * @param commands A list of commands to be registered
     */
    public void registerCommand(@NotNull final CommandBase... commands) {
        for (final CommandBase command : commands) {
            registerCommand(command);
        }
    }

}
