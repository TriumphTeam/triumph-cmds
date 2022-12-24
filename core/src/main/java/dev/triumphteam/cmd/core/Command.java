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

import dev.triumphteam.cmd.core.argument.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static dev.triumphteam.cmd.core.processor.Commands.nameOf;

/**
 * Command interface which all platforms will implement.
 *
 * @param <S> The sender type.
 */
public interface Command<S> {

    @NotNull Map<String, SubCommand<S>> getSubCommands();

    @NotNull Map<String, SubCommand<S>> getSubCommandAlias();

    void addSubCommand(
            final @NotNull String name,
            final @NotNull SubCommand<S> subCommand,
            final boolean isAlias
    );

    default @Nullable SubCommand<S> getSubCommand(final @NotNull List<String> args) {
        SubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.size() > 0) subCommandName = args.get(0).toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        // TODO
        /*if (subCommand == null || (args.size() > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            return null;
        }*/

        return subCommand;
    }

    default @Nullable SubCommand<S> getDefaultSubCommand() {
        return getSubCommands().get(dev.triumphteam.cmd.core.annotation.Command.DEFAULT_CMD_NAME);
    }

    default @Nullable SubCommand<S> getSubCommand(final @NotNull String key) {
        final SubCommand<S> subCommand = getSubCommands().get(key);
        if (subCommand != null) return subCommand;
        return getSubCommandAlias().get(key);
    }

    default boolean subCommandExists(final @NotNull String key) {
        return getSubCommands().containsKey(key) || getSubCommandAlias().containsKey(key);
    }
}
