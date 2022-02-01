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

import dev.triumphteam.cmd.core.annotation.Default;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Sub command holds its data and its execution.
 *
 * @param <S> The sender type.
 */
public interface SubCommand<S> {

    // TODO: 1/30/2022
    boolean isNamedArguments();

    /**
     * Checks if the sub command is default.
     * Can also just check if the name is {@link Default#DEFAULT_CMD_NAME}.
     *
     * @return Whether the sub command is default.
     */
    boolean isDefault();

    /**
     * Executes the sub command.
     *
     * @param sender The sender.
     * @param args   The arguments to pass to the executor.
     */
    void execute(@NotNull S sender, @NotNull final List<String> args);

}
