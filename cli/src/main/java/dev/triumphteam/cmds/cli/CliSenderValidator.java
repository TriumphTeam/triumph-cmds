package dev.triumphteam.cmds.cli;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmds.cli.sender.CliSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

final class CliSenderValidator implements SenderValidator<CliSender> {

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Set<Class<? extends CliSender>> getAllowedSenders() {
        return ImmutableSet.of(CliSender.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(
            @NotNull final MessageRegistry<CliSender> messageRegistry,
            @NotNull final SubCommand<CliSender> subCommand,
            @NotNull final CliSender sender
    ) {
        return true;
    }
}
