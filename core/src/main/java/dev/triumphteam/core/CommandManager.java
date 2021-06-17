package dev.triumphteam.core;

import dev.triumphteam.core.command.argument.ArgumentResolver;
import dev.triumphteam.core.registry.ArgumentRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Base command manager for all platforms
 */
public abstract class CommandManager<S> {

    private final ArgumentRegistry<S> argumentRegistry = new ArgumentRegistry<>();

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

    public final void registerArgument(@NotNull final Class<?> clazz, @NotNull final ArgumentResolver<S> resolver) {
        argumentRegistry.register(clazz, resolver);
    }

    protected ArgumentRegistry<S> getArgumentRegistry() {
        return argumentRegistry;
    }

}
