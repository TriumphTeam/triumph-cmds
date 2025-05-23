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
package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public interface CommandExecutor<S> {

    void execute(
            final @NotNull CommandMeta meta,
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull S sender,
            final @NotNull Object instance,
            final @NotNull Method method,
            final @NotNull List<Object> arguments
    ) throws Throwable;

    default void handleResult(
            final @NotNull CommandMeta meta,
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull S sender,
            final @Nullable Object result
    ) {
        // We don't care about nulls.
        if (result == null) return;
        // We don't care if the return is not a CommandExecuteResult.
        if (!(result instanceof CommandExecuteResult)) return;

        // We also don't care if everything went well.
        if (!(result instanceof CommandExecuteResult.Failure)) return;

        try {
            // If we can't cast, we can just ignore it.
            //noinspection unchecked
            ((CommandExecuteResult.Failure<S>) result).sendMessage(messageRegistry, sender, meta);
        } catch (ClassCastException ignored) {}
    }
}
