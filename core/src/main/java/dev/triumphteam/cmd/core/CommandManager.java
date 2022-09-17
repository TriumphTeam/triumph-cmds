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
import dev.triumphteam.cmd.core.argument.named.Argument;
import dev.triumphteam.cmd.core.argument.named.ArgumentKey;
import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Base command manager for all platforms.
 *
 * @param <DS> The default sender type.
 * @param <S>  The sender type.
 */
@SuppressWarnings("unchecked")
public abstract class CommandManager<DS, S> {

    private final SenderMapper<DS, S> senderMapper;
    private final SenderValidator<S> senderValidator;

    public CommandManager(
            final @NotNull SenderMapper<DS, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator
    ) {
        this.senderMapper = senderMapper;
        this.senderValidator = senderValidator;
    }

    /**
     * Registers a {@link BaseCommand} into the manager.
     *
     * @param baseCommand The {@link BaseCommand} to be registered.
     */
    public abstract void registerCommand(final @NotNull BaseCommand baseCommand);

    /**
     * Registers {@link BaseCommand}s.
     *
     * @param baseCommands A list of baseCommands to be registered.
     */
    public final void registerCommand(final @NotNull BaseCommand @NotNull ... baseCommands) {
        for (final BaseCommand command : baseCommands) {
            registerCommand(command);
        }
    }

    /**
     * Main method for unregistering commands to be implemented in other platform command managers.
     *
     * @param command The {@link BaseCommand} to be unregistered.
     */
    public abstract void unregisterCommand(final @NotNull BaseCommand command);

    /**
     * Method to unregister commands with vararg.
     *
     * @param commands A list of commands to be unregistered.
     */
    public final void unregisterCommands(final @NotNull BaseCommand @NotNull ... commands) {
        for (final BaseCommand command : commands) {
            unregisterCommand(command);
        }
    }

    /**
     * Registers a custom internalArgument.
     *
     * @param clazz    The class of the internalArgument to be registered.
     * @param resolver The {@link ArgumentResolver} with the internalArgument resolution.
     */
    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull ArgumentResolver<S> resolver) {
        getRegistryContainer().getArgumentRegistry().register(clazz, resolver);
    }

    // TODO: Comments
    public void registerSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver<S> suggestionResolver) {
        getRegistryContainer().getSuggestionRegistry().register(key, suggestionResolver);
    }

    // TODO: Comments
    public void registerSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver<S> suggestionResolver) {
        getRegistryContainer().getSuggestionRegistry().register(type, suggestionResolver);
    }

    // TODO: Comments
    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull Argument @NotNull ... arguments) {
        registerNamedArguments(key, Arrays.asList(arguments));
    }

    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull List<@NotNull Argument> arguments) {
        getRegistryContainer().getNamedArgumentRegistry().register(key, arguments);
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

    // TODO: Comments
    protected abstract @NotNull RegistryContainer<S> getRegistryContainer();

    // TODO: 2/4/2022 comments
    protected @NotNull SenderMapper<DS, S> getSenderMapper() {
        return senderMapper;
    }

    protected @NotNull SenderValidator<S> getSenderValidator() {
        return senderValidator;
    }
}
