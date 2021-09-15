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
package dev.triumphteam.cmds.core.requirement;

import dev.triumphteam.cmds.core.message.MessageKey;
import dev.triumphteam.cmds.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Requirement<S> {

    private final RequirementResolver<S> resolver;
    private final MessageKey<MessageContext> messageKey;

    public Requirement(
            @NotNull final RequirementResolver<S> resolver,
            @Nullable final MessageKey<MessageContext> messageKey
    ) {
        this.resolver = resolver;
        this.messageKey = messageKey;
    }

    @Nullable
    public MessageKey<MessageContext> getMessageKey() {
        return messageKey;
    }

    public boolean test(@NotNull final S sender) {
        return resolver.resolve(sender);
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "resolver=" + resolver +
                ", messageKey=" + messageKey +
                '}';
    }
}
