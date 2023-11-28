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
package dev.triumphteam.cmd.core.extention.sender;

import dev.triumphteam.cmd.core.extention.ValidationResult;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface SenderExtension<D, S> extends SenderMapper<D, S> {

    @NotNull Set<Class<? extends S>> getAllowedSenders();

    @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(
            final @NotNull CommandMeta meta,
            final @NotNull Class<?> allowedSender,
            final @NotNull S sender
    );

    interface Default<S> extends SenderExtension<S, S> {

        @Override
        default @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(
                final @NotNull CommandMeta meta,
                final @NotNull Class<?> allowedSender,
                final @NotNull S sender
        ) {
            return valid();
        }

        @Override
        default @NotNull S map(final @NotNull S defaultSender) {
            return defaultSender;
        }

        @Override
        default @NotNull S mapBackwards(final @NotNull S sender) {
            return sender;
        }
    }

    default ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> valid() {
        return new ValidationResult.Valid<>();
    }

    default ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> invalid(
            final @NotNull MessageKey<@NotNull MessageContext> messageKey
    ) {
        return new ValidationResult.Invalid<>(messageKey);
    }
}
