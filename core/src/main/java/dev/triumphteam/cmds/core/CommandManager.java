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
package dev.triumphteam.cmds.core;

import dev.triumphteam.cmds.core.argument.ArgumentRegistry;
import dev.triumphteam.cmds.core.argument.ArgumentResolver;
import dev.triumphteam.cmds.core.message.context.MessageContext;
import dev.triumphteam.cmds.core.message.MessageKey;
import dev.triumphteam.cmds.core.message.MessageRegistry;
import dev.triumphteam.cmds.core.message.MessageResolver;
import dev.triumphteam.cmds.core.requirement.RequirementKey;
import dev.triumphteam.cmds.core.requirement.RequirementRegistry;
import dev.triumphteam.cmds.core.requirement.RequirementResolver;
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

    public final <C extends MessageContext> void registerMessage(
            @NotNull final MessageKey<C> key,
            @NotNull final MessageResolver<S, C> resolver
    ) {
        messageRegistry.register(key, resolver);
    }

    public final void registerRequirement(
            @NotNull final RequirementKey key,
            @NotNull final RequirementResolver<S> resolver
    ) {
        requirementRegistry.register(key, resolver);
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
