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
package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Contains the data for the requirement.
 *
 * @param <S> The sender type.
 */
public final class Requirement<S, C extends MessageContext> {

    private final RequirementResolver<S> resolver;
    private final ContextualKey<C> messageKey;
    private final Function<CommandMeta, C> contextFactory;
    private final boolean invert;

    public Requirement(
            final @NotNull RequirementResolver<S> resolver,
            final @Nullable ContextualKey<C> messageKey,
            final @NotNull Function<CommandMeta, C> contextFactory,
            final boolean invert
    ) {
        this.resolver = resolver;
        this.messageKey = messageKey;
        this.contextFactory = contextFactory;
        this.invert = invert;
    }

    /**
     * The message key which will be used to send the defined message to the sender.
     *
     * @return The message key or null if no message should be sent.
     */
    public @Nullable ContextualKey<C> getMessageKey() {
        return messageKey;
    }

    // TODO
    public void sendMessage(
            final @NotNull MessageRegistry<S> registry,
            final @NotNull S sender,
            final @NotNull CommandMeta meta
    ) {
        if (messageKey == null) return;
        registry.sendMessage(messageKey, sender, contextFactory.apply(meta));
    }

    /**
     * Checks if the requirement is met or not.
     *
     * @param sender The sender which will be needed to check if the requirement is met or not.
     * @return Whether the requirement is met.
     */
    public boolean isMet(final @NotNull S sender) {
        return resolver.resolve(sender) != invert;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Requirement<?, ?> that = (Requirement<?, ?>) o;
        return resolver.equals(that.resolver) && Objects.equals(messageKey, that.messageKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolver, messageKey);
    }

    @Override
    public @NotNull String toString() {
        return "Requirement{" +
                "resolver=" + resolver +
                ", messageKey=" + messageKey +
                '}';
    }
}
