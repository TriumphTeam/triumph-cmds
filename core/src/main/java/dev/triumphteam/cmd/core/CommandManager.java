/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core;

import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Base command manager for all platforms.
 *
 * @param <D> The default sender type.
 * @param <S> The sender type.
 */
public abstract class CommandManager<D, S> {

    private final CommandOptions<D, S> commandOptions;

    public CommandManager(final @NotNull CommandOptions<D, S> commandOptions) {
        this.commandOptions = commandOptions;
    }

    /**
     * Registers a command into the manager.
     *
     * @param command The instance of the command to be registered.
     */
    public abstract void registerCommand(final @NotNull Object command);

    /**
     * Registers commands.
     *
     * @param commands A list of commands to be registered.
     */
    public final void registerCommand(final @NotNull Object @NotNull ... commands) {
        for (final Object command : commands) {
            registerCommand(command);
        }
    }

    /**
     * Main method for unregistering commands to be implemented in other platform command managers.
     *
     * @param command The command to be unregistered.
     */
    public abstract void unregisterCommand(final @NotNull Object command);

    /**
     * Method to unregister commands with vararg.
     *
     * @param commands A list of commands to be unregistered.
     */
    public final void unregisterCommands(final @NotNull Object @NotNull ... commands) {
        for (final Object command : commands) {
            unregisterCommand(command);
        }
    }

    /**
     * Registers a custom argument.
     *
     * @param clazz    The class of the argument to be registered.
     * @param resolver The {@link ArgumentResolver} with the argument resolution.
     */
    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull ArgumentResolver<S> resolver) {
        getRegistryContainer().getArgumentRegistry().register(clazz, resolver);
    }

    /**
     * Registers a suggestion to be used for specific arguments.
     *
     * @param key      The {@link SuggestionKey} that identifies the registered suggestion.
     * @param resolver The {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver<S> resolver) {
        getRegistryContainer().getSuggestionRegistry().register(key, resolver);
    }

    /**
     * Registers a suggestion to be used for all arguments of a specific type.
     *
     * @param type     Using specific {@link Class} types as target for suggestions instead of keys.
     * @param resolver The {@link SuggestionResolver} with the suggestion resolution.
     */
    public void registerSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver<S> resolver) {
        getRegistryContainer().getSuggestionRegistry().register(type, resolver);
    }

    /**
     * Registers a list of arguments to be used as named arguments in a command.
     *
     * @param key       The {@link ArgumentKey} to represent the list.
     * @param arguments The list of arguments.
     */
    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull Argument @NotNull ... arguments) {
        registerNamedArguments(key, Arrays.asList(arguments));
    }

    /**
     * Registers a list of arguments to be used on a {@link Arguments} argument in a command.
     *
     * @param key       The {@link ArgumentKey} to represent the list.
     * @param arguments The {@link List} of arguments.
     */
    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull List<Argument> arguments) {
        getRegistryContainer().getNamedArgumentRegistry().register(key, arguments);
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key   The {@link FlagKey} to represent the list.
     * @param flags The list of flags.
     */
    public final void registerFlags(final @NotNull FlagKey key, final @NotNull Flag @NotNull ... flags) {
        registerFlags(key, Arrays.asList(flags));
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key   The {@link FlagKey} to represent the list.
     * @param flags The {@link List} of flags.
     */
    public final void registerFlags(final @NotNull FlagKey key, final @NotNull List<Flag> flags) {
        getRegistryContainer().getFlagRegistry().register(key, flags);
    }

    /**
     * Registers a custom message.
     *
     * @param key      The {@link ContextualKey} of the message to be registered.
     * @param resolver The {@link ArgumentResolver} with the message sending resolution.
     */
    public final <C extends MessageContext> void registerMessage(
            final @NotNull ContextualKey<C> key,
            final @NotNull MessageResolver<S, C> resolver
    ) {
        getRegistryContainer().getMessageRegistry().register(key, resolver);
    }

    /**
     * Registers a requirement.
     *
     * @param key      The {@link RequirementKey} of the requirement to be registered.
     * @param resolver The {@link ArgumentResolver} with the requirement resolution.
     */
    public final void registerRequirement(
            final @NotNull RequirementKey key,
            final @NotNull RequirementResolver<S> resolver
    ) {
        getRegistryContainer().getRequirementRegistry().register(key, resolver);
    }

    protected abstract @NotNull RegistryContainer<S> getRegistryContainer();

    protected @NotNull CommandOptions<D, S> getCommandOptions() {
        return commandOptions;
    }
}
