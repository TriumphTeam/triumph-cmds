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

import dev.triumphteam.cmd.core.annotation.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class in which all commands need to extend.
 */
public abstract class BaseCommand {

    private final String command;
    private final List<String> alias = new ArrayList<>();

    /**
     * Normal constructor with no arguments for when using the {@link Command} annotation
     */
    public BaseCommand() {
        this(null, null);
    }

    /**
     * Constructor for alias only
     *
     * @param alias The alias {@link List}
     */
    public BaseCommand(final @Nullable List<@NotNull String> alias) {
        this(null, alias);
    }

    /**
     * Constructor for command name only
     *
     * @param command The command name
     */
    public BaseCommand(final @Nullable String command) {
        this(command, null);
    }

    /**
     * Secondary constructor for when wanting a more customizable command, which isn't possible with annotations
     *
     * @param command The command name
     * @param alias   The aliases for the command
     */
    public BaseCommand(final @Nullable String command, final @Nullable List<@NotNull String> alias) {
        this.command = command;
        if (alias != null) {
            this.alias.addAll(alias);
        }
    }

    /**
     * Gets the command name
     *
     * @return The {@link #command}
     */
    public @Nullable String getCommand() {
        return command;
    }

    /**
     * Gets the list with the aliases for the command
     *
     * @return The {@link #alias}
     */
    public @NotNull List<@NotNull String> getAlias() {
        return alias;
    }

}
