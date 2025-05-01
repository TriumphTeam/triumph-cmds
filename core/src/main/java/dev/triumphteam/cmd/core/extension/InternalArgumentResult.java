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
package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public interface InternalArgumentResult {

    final class Valid implements InternalArgumentResult {

        private final Object value;

        public Valid(final @NotNull Object value) {
            this.value = value;
        }

        public @NotNull Object getValue() {
            return value;
        }
    }

    final class Invalid implements InternalArgumentResult {

        private final BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext> fail;

        public Invalid(final @NotNull BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext> fail) {
            this.fail = fail;
        }

        public @NotNull BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext> getFail() {
            return fail;
        }
    }
}
