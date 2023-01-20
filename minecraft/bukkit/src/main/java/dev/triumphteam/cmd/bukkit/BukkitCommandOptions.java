package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandOptions<S> extends CommandOptions<CommandSender, S> {

    public BukkitCommandOptions(
            final @NotNull SenderExtension<CommandSender, S> senderExtension,
            final @NotNull CommandExtensions<CommandSender, S> commandExtensions
    ) {
        super(senderExtension, commandExtensions);
    }

    public static final class Builder<S> extends CommandOptions.Builder<CommandSender, S, BukkitCommandOptions<S>, Builder<S>> {

        @Override
        public @NotNull BukkitCommandOptions<S> build(final @NotNull SenderExtension<CommandSender, S> senderExtension) {
            return new BukkitCommandOptions<>(senderExtension, getCommandExtensions());
        }
    }
}
