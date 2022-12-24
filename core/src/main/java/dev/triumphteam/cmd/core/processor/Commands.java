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
package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

public final class Commands {

    private Commands() {
        throw new AssertionError("Class cannot be instantiated.");
    }

    /**
     * Gets the name of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The name of the command.
     */
    public static @NotNull String nameOf(final @NotNull BaseCommand baseCommand) {
        final Class<? extends @NotNull BaseCommand> commandClass = baseCommand.getClass();
        final Command commandAnnotation = commandClass.getAnnotation(Command.class);

        final String name;
        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or \"@" + Command.class.getSimpleName() + "\" annotation missing", baseCommand.getClass());
            }

            name = commandName;
        } else {
            name = commandAnnotation.value();
        }

        if (name.isEmpty() || name.equals(Command.DEFAULT_CMD_NAME)) {
            throw new CommandRegistrationException("Command name must not be empty", baseCommand.getClass());
        }

        return name;
    }

    /**
     * Gets the alias of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The alias of the command.
     */
    public static @NotNull List<String> aliasOf(final @NotNull BaseCommand baseCommand) {
        final Command commandAnnotation = baseCommand.getClass().getAnnotation(Command.class);
        return commandAnnotation == null ? baseCommand.getAlias() : Arrays.asList(commandAnnotation.alias());
    }

    /**
     * Gets the alias of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The alias of the command.
     */
    public static @NotNull String descriptionOf(final @NotNull BaseCommand baseCommand) {
        final Description commandAnnotation = baseCommand.getClass().getAnnotation(Description.class);
        return commandAnnotation == null ? baseCommand.getDescription() : commandAnnotation.value();
    }

    public static @Nullable String nameOf(final @NotNull AnnotatedElement element) {
        final Command commandAnnotation = element.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        return commandAnnotation.value();
    }
}
