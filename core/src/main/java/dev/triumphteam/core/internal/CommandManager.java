package dev.triumphteam.core.internal;

import dev.triumphteam.core.internal.command.Command;
import dev.triumphteam.core.internal.registry.ArgumentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base command manager for all platforms
 */
public abstract class CommandManager<C extends Command> {

    private final Map<String, C> commands = new LinkedHashMap<>();

    private final ArgumentRegistry argumentRegistry = new ArgumentRegistry();

    /**
     * Main registering method to be implemented in other platform command managers
     *
     * @param command The {@link BaseCommand} to be registered
     */
    public abstract void registerCommand(@NotNull final BaseCommand command);

    /**
     * Method to register commands with vararg
     *
     * @param commands A list of commands to be registered
     */
    public final void registerCommand(@NotNull final BaseCommand... commands) {
        for (final BaseCommand command : commands) {
            registerCommand(command);
        }
    }

    /**
     * To be called by manager implementations, registers the command in the {@link #commands} map
     *
     * @param commandName The command name
     * @param command     The {@link Command} implementation
     */
    protected void register(@NotNull final String commandName, @NotNull final C command) {
        if (commands.containsKey(commandName)) {
            // register subcommands from other classes
            return;
        }

        commands.put(commandName, command);
    }

}
