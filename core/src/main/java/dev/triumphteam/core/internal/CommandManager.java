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
    private final Map<String, Command> commands = new LinkedHashMap<>();

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

    /**
     * To be called by manager implementations, registers the command in the {@link #commands} map
     *
     * @param commandName The command name
     * @param command     The {@link Command} implementation
     */
    protected void register(@NotNull final String commandName, @NotNull final Command command) {
        if (commands.containsKey(commandName)) {
            // register subcommands from other classes
            return;
        }

        commands.put(commandName, command);
    }

}
