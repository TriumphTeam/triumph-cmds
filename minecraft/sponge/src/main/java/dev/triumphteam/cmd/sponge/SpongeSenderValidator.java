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
