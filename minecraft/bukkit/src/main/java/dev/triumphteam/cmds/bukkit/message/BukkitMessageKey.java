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
package dev.triumphteam.cmds.bukkit.message;

import dev.triumphteam.cmds.core.key.RegistryKey;
import dev.triumphteam.cmds.core.message.context.MessageContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * {@link BukkitMessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public final class BukkitMessageKey<C extends MessageContext> extends RegistryKey {

    // Default keys
    public static final BukkitMessageKey<MessageContext> NO_PERMISSION = of("no.Permission", MessageContext.class);
    // Holds all registered keys, default and custom ones
    private static final Set<BukkitMessageKey<? extends MessageContext>> REGISTERED_KEYS = new HashSet<>();
    private final Class<C> type;

    private BukkitMessageKey(@NotNull final String key, @NotNull final Class<C> type) {
        super(key);
        this.type = type;

        REGISTERED_KEYS.add(this);
    }

    /**
     * Factory method for creating a {@link BukkitMessageKey}.
     *
     * @param key  The value of the key, normally separated by <code>.</code>.
     * @param type The {@link MessageContext} type.
     * @param <C>  Generic {@link MessageContext} type.
     * @return A new {@link BukkitMessageKey} for a specific {@link MessageContext}.
     */
    @NotNull
    @Contract("_, _ -> new")
    public static <C extends MessageContext> BukkitMessageKey<C> of(@NotNull final String key, @NotNull final Class<C> type) {
        return new BukkitMessageKey<>(key, type);
    }

    /**
     * Gets an immutable {@link Set} with all the registered keys.
     *
     * @return The keys {@link Set}.
     */
    @NotNull
    public static Set<BukkitMessageKey<? extends MessageContext>> getRegisteredKeys() {
        return Collections.unmodifiableSet(REGISTERED_KEYS);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final BukkitMessageKey<?> that = (BukkitMessageKey<?>) o;
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
                ", super=" + super.toString() + "}";
    }
}
