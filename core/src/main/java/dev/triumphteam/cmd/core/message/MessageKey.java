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
package dev.triumphteam.cmd.core.message;

import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * {@link MessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public class MessageKey<C extends MessageContext> extends ContextualKey<C> {

    // Default keys
    public static final MessageKey<InvalidCommandContext> UNKNOWN_COMMAND = of("unknown.command", InvalidCommandContext.class);
    public static final MessageKey<MessageContext> TOO_MANY_ARGUMENTS = of("too.many.arguments", MessageContext.class);
    public static final MessageKey<MessageContext> NOT_ENOUGH_ARGUMENTS = of("not.enough.arguments", MessageContext.class);
    public static final MessageKey<InvalidArgumentContext> INVALID_ARGUMENT = of("invalid.argument", InvalidArgumentContext.class);

    protected MessageKey(final @NotNull String key, final @NotNull Class<C> type) {
        super(key, type);
    }

    /**
     * Factory method for creating a {@link MessageKey}.
     *
     * @param key  The value of the key, normally separated by <code>.</code>.
     * @param type The {@link MessageContext} type.
     * @param <C>  Generic {@link MessageContext} type.
     * @return A new {@link MessageKey} for a specific {@link MessageContext}.
     */
    @Contract("_, _ -> new")
    public static <C extends MessageContext> @NotNull MessageKey<C> of(final @NotNull String key, final @NotNull Class<C> type) {
        return new MessageKey<>(key, type);
    }
}
