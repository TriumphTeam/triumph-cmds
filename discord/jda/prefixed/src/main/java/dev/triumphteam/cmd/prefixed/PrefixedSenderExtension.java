/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.prefixed;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Simple mapper than returns itself.
 */
class PrefixedSenderExtension implements SenderExtension<PrefixedSender, PrefixedSender> {

    @Override
    public @NotNull Set<Class<? extends PrefixedSender>> getAllowedSenders() {
        return ImmutableSet.of(PrefixedSender.class);
    }

    @Override
    public @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(final @NotNull CommandMeta meta, final @NotNull Class<?> allowedSender, final @NotNull PrefixedSender sender) {
        return valid();
    }

    @Override
    public @NotNull PrefixedSender map(final @NotNull PrefixedSender defaultSender) {
        return defaultSender;
    }

    @Override
    public @NotNull PrefixedSender mapBackwards(final @NotNull PrefixedSender sender) {
        return sender;
    }
}
