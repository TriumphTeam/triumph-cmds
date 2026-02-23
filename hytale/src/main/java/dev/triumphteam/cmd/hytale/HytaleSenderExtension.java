package dev.triumphteam.cmd.hytale;

import com.google.common.collect.ImmutableSet;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HytaleSenderExtension implements SenderExtension.Default<CommandSender> {

    @Override
    public @NotNull Set<Class<? extends CommandSender>> getAllowedSenders() {
        return ImmutableSet.of(CommandSender.class);
    }

    @Override
    public @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(
            final @NotNull CommandMeta meta,
            final @NotNull Class<?> allowedSender,
            final @NotNull CommandSender sender
    ) {
        /*if (Player.class.isAssignableFrom(allowedSender) && !(sender instanceof Player)) {
            return invalid(BukkitMessageKey.PLAYER_ONLY);
        }

        if (ConsoleCommandSender.class.isAssignableFrom(allowedSender) && !(sender instanceof ConsoleCommandSender)) {
            return invalid(BukkitMessageKey.CONSOLE_ONLY);
        }*/

        return valid();
    }
}
