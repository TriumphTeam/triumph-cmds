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
package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMetaContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Representation of a command.
 * Commands can either be command holders and commands themselves.
 * Some holders are {@link InternalParentCommand}s.
 * And actual commands can be for example {@link InternalLeafCommand}.
 */
public interface InternalCommand<D, S, ST> extends CommandMetaContainer {

    String DEFAULT_CMD_NAME = "th-default";
    String PARENT_CMD_WITH_ARGS_NAME = "th-args-cmd";

    @NotNull Settings<D, S> getCommandSettings();

    /**
     * @return The name of the command.
     */
    @NotNull String getName();

    /**
     * @return The command's description.
     */
    @NotNull String getDescription();

    /**
     * @return A list with all of its aliases.
     */
    @NotNull List<String> getAliases();

    /**
     * @return Whether this is a "default" command, meaning it represents the class itself and is not separate.
     */
    boolean isDefault();

    boolean isHidden();

    /**
     * @return Whether the command has arguments.
     */
    boolean hasArguments();

    /**
     * @return The command's syntax.
     */
    @NotNull String getSyntax();
}
