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

import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Base command manager for all platforms.
 *
 * @param <D> The default sender type.
 * @param <S> The sender type.
 */
public abstract class CommandManager<D, S, O extends CommandOptions<D, S, O, ST>, ST> extends ManagerSetup<D, S, O, ST> {

    public CommandManager(
            final @NotNull O commandOptions,
            final @NotNull RegistryContainer<D, S, ST> registryContainer
    ) {
        super(registryContainer, commandOptions);
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
    public final void registerCommands(final @NotNull Object @NotNull ... commands) {
        for (final Object command : commands) {
            registerCommand(command);
        }
    }

    /**
     * The main method for unregistering commands to be implemented in other platform command managers.
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
}
