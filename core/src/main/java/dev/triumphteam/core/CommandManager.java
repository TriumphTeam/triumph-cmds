package dev.triumphteam.core;

import dev.triumphteam.core.command.argument.ArgumentResolver;
import dev.triumphteam.core.command.message.MessageResolver;
import dev.triumphteam.core.command.argument.ArgumentRegistry;
import dev.triumphteam.core.command.message.MessageRegistry;
import dev.triumphteam.core.command.requirement.RequirementRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Base command manager for all platforms
 */
public abstract class CommandManager<S> {

    private final ArgumentRegistry<S> argumentRegistry = new ArgumentRegistry<>();
    private final RequirementRegistry<S> requirementRegistry = new RequirementRegistry<>();
    private final MessageRegistry<S> messageRegistry = new MessageRegistry<>();

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
     * Main method for unregistering commands to be implemented in other platform command managers
     *
     * @param command The {@link BaseCommand} to be unregistered
     */
    public abstract void unregisterCommand(@NotNull final BaseCommand command);

    /**
     * Method to unregister commands with vararg
     *
     * @param commands A list of commands to be unregistered
     */
    public final void unregisterCommands(@NotNull final BaseCommand... commands) {
        for (final BaseCommand command : commands) {
            unregisterCommand(command);
        }
    }

    public final void registerArgument(@NotNull final Class<?> clazz, @NotNull final ArgumentResolver<S> resolver) {
        argumentRegistry.register(clazz, resolver);
    }

    public final void registerMessage(@NotNull final String key, @NotNull final MessageResolver<S> resolver) {
        messageRegistry.register(key, resolver);
    }

    protected ArgumentRegistry<S> getArgumentRegistry() {
        return argumentRegistry;
    }

    protected RequirementRegistry<S> getRequirementRegistry() {
        return requirementRegistry;
    }

    protected MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

}
