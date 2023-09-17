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
package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Context for when user types an invalid argument based on its type.
 */
public final class InvalidArgumentContext extends InvalidInputContext {

    private final String syntax;
    private final String name;
    private final Class<?> type;

    public InvalidArgumentContext(
            final @NotNull CommandMeta meta,
            final @NotNull String syntax,
            final @Nullable String invalidInput,
            final @NotNull String name,
            final @NotNull Class<?> type
    ) {
        super(meta, invalidInput);
        this.syntax = syntax;
        this.name = name;
        this.type = type;
    }

    public @NotNull String getSyntax() {
        return syntax;
    }

    public @NotNull String getArgumentName() {
        return name;
    }

    public @NotNull Class<?> getArgumentType() {
        return type;
    }
}
