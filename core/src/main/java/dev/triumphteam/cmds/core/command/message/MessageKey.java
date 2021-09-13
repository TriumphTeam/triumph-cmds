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
package dev.triumphteam.cmds.core.command.message;

import dev.triumphteam.cmds.core.command.message.context.DefaultMessageContext;
import dev.triumphteam.cmds.core.command.message.context.InvalidArgumentContext;
import dev.triumphteam.cmds.core.command.message.context.InvalidFlagArgumentContext;
import dev.triumphteam.cmds.core.command.message.context.MessageContext;
import dev.triumphteam.cmds.core.command.message.context.MissingFlagArgumentContext;
import dev.triumphteam.cmds.core.command.message.context.MissingFlagContext;
import dev.triumphteam.cmds.core.key.RegistryKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * {@link MessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public final class MessageKey<C extends MessageContext> extends RegistryKey {

    // Holds all registered keys, default and custom ones
    private static final Set<MessageKey<? extends MessageContext>> REGISTERED_KEYS = new HashSet<>();

    // Default keys

    public static final MessageKey<MessageContext> UNKNOWN_COMMAND = of("unknown.command", MessageContext.class);
    public static final MessageKey<DefaultMessageContext> TOO_MANY_ARGUMENTS = of("too.many.arguments", DefaultMessageContext.class);
    public static final MessageKey<DefaultMessageContext> NOT_ENOUGH_ARGUMENTS = of("not.enough.arguments", DefaultMessageContext.class);
    public static final MessageKey<InvalidArgumentContext> INVALID_ARGUMENT = of("invalid.argument", InvalidArgumentContext.class);
    // Flag related
    public static final MessageKey<MissingFlagContext> MISSING_REQUIRED_FLAG = of("missing.required.flag", MissingFlagContext.class);
    public static final MessageKey<MissingFlagArgumentContext> MISSING_REQUIRED_FLAG_ARGUMENT = of("missing.required.flag.argument", MissingFlagArgumentContext.class);
    public static final MessageKey<InvalidFlagArgumentContext> INVALID_FLAG_ARGUMENT = of("invalid.flag.argument", InvalidFlagArgumentContext.class);

    private final Class<C> type;

    private MessageKey(@NotNull final String key, @NotNull final Class<C> type) {
        super(key);
        this.type = type;

        REGISTERED_KEYS.add(this);
    }

    /**
     * Factory method for creating a {@link MessageKey}.
     *
     * @param key  The value of the key, normally separated by <code>.</code>.
     * @param type The {@link MessageContext} type.
     * @param <C>  Generic {@link MessageContext} type.
     * @return A new {@link MessageKey} for a specific {@link MessageContext}.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static <C extends MessageContext> MessageKey<C> of(@NotNull final String key, @NotNull final Class<C> type) {
        return new MessageKey<>(key, type);
    }

    /**
     * Getter for the {@link MessageContext} type, in case it's needed.
     *
     * @return The {@link MessageContext} type.
     */
    @NotNull
    public Class<C> getType() {
        return type;
    }

    /**
     * Gets an immutable {@link Set} with all the registered keys.
     *
     * @return The keys {@link Set}.
     */
    @NotNull
    public static Set<MessageKey<? extends MessageContext>> getRegisteredKeys() {
        return Collections.unmodifiableSet(REGISTERED_KEYS);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final MessageKey<?> that = (MessageKey<?>) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public String toString() {
        return "MessageKey{" +
                "type=" + type +
                "} " + super.toString();
    }
}
