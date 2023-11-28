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
package dev.triumphteam.cmd.bukkit.message;

import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * {@link BukkitMessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public final class BukkitMessageKey<C extends MessageContext> extends MessageKey<C> {

    // Default keys
    public static final BukkitMessageKey<NoPermissionMessageContext> NO_PERMISSION = new BukkitMessageKey<>("no.permission", NoPermissionMessageContext.class);
    public static final BukkitMessageKey<MessageContext> PLAYER_ONLY = new BukkitMessageKey<>("player.only", MessageContext.class);
    public static final BukkitMessageKey<MessageContext> CONSOLE_ONLY = new BukkitMessageKey<>("console.only", MessageContext.class);

    private BukkitMessageKey(final @NotNull String key, final @NotNull Class<C> type) {
        super(key, type);
    }

    @Override
    public @NotNull String toString() {
        return "BukkitMessageKey{super=" + super.toString() + "}";
    }
}
