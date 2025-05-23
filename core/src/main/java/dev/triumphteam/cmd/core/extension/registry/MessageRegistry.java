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
package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.MessageSender;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry with all the messages that'll be sent to the user.
 *
 * @param <S> Sender type.
 */
public final class MessageRegistry<S> implements MessageSender<S>,  Registry {

    private final Map<ContextualKey<?>, MessageResolver<S, ? extends MessageContext>> messages = new HashMap<>();

    /**
     * Registers a new message to be used by the plugin.
     *
     * @param key      A {@link ContextualKey} which will identify the {@link MessageResolver}.
     * @param resolver The {@link MessageResolver} which contains the message sending methods.
     * @param <C>      The type of  {@link MessageContext} used by the message.
     */
    public <C extends MessageContext> void register(
            final @NotNull ContextualKey<C> key,
            final @NotNull MessageResolver<S, C> resolver
    ) {
        messages.put(key, resolver);
    }

    /**
     * Sends a message to the sender based on the {@link MessageKey}.
     *
     * @param key     The {@link MessageKey} to get the correct {@link MessageResolver}.
     * @param sender  A {@link S} sender, which will receive the message.
     * @param context The {@link MessageContext} generated by the command executor.
     * @param <C>     The type of {@link MessageContext} to be used.
     */
    @Override
    public <C extends MessageContext> void sendMessage(
            final @NotNull MessageKey<C> key,
            final @NotNull S sender,
            final @NotNull C context
    ) {
        //noinspection unchecked
        final MessageResolver<S, C> messageResolver = (MessageResolver<S, C>) messages.get(key);
        if (messageResolver == null) return;
        messageResolver.resolve(sender, context);
    }

}
