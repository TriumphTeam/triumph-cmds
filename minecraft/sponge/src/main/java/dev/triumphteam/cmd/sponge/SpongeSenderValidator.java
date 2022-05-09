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
package dev.triumphteam.cmd.sponge;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.sponge.message.SpongeMessageKey;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

import java.util.Set;

class SpongeSenderValidator implements SenderValidator<Subject> {


    @NotNull
    @Override
    public Set<Class<? extends Subject>> getAllowedSenders() {
        return ImmutableSet.of(ServerPlayer.class, SystemSubject.class);
    }

    @Override
    public boolean validate(
            @NotNull MessageRegistry<Subject> messageRegistry,
            @NotNull SubCommand<Subject> subCommand,
            @NotNull Subject sender
    ) {
        final Class<? extends Subject> senderClass = subCommand.getSenderType();

        if(ServerPlayer.class.isAssignableFrom(senderClass) && !(sender instanceof ServerPlayer)) {
            messageRegistry.sendMessage(
                    SpongeMessageKey.PLAYER_ONLY,
                    sender,
                    new DefaultMessageContext(subCommand.getParentName(), subCommand.getName())
            );
            return false;
        }

        if (SystemSubject.class.isAssignableFrom(senderClass) && !(sender instanceof SystemSubject)) {
            messageRegistry.sendMessage(
                    SpongeMessageKey.CONSOLE_ONLY,
                    sender,
                    new DefaultMessageContext(subCommand.getParentName(), subCommand.getName())
            );
            return false;
        }

        return true;
    }
}
