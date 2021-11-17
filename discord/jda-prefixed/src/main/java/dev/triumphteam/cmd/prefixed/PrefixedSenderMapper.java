package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;

public class PrefixedSenderMapper implements SenderMapper<PrefixedSender, PrefixedSender> {

    @Override
    public void validate(@NotNull final Class<?> senderClass) {
        if (!senderClass.isAssignableFrom(PrefixedSender.class)) {
            throw new RuntimeException("Invalid or missing sender parameter (must be a PrefixedCommandSender).");
        }
    }

    @Override
    public PrefixedSender map(@NotNull final PrefixedSender defaultSender) {
        return defaultSender;
    }

}
