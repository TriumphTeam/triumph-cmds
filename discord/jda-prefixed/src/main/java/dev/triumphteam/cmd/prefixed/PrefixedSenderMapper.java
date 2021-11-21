package dev.triumphteam.cmd.prefixed;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PrefixedSenderMapper implements SenderMapper<PrefixedSender, PrefixedSender> {

    @NotNull
    @Override
    public Set<Class<? extends PrefixedSender>> getAllowedSenders() {
        return ImmutableSet.of(PrefixedSender.class);
    }

    @Nullable
    @Override
    public PrefixedSender map(@NotNull final PrefixedSender defaultSender) {
        return defaultSender;
    }

}
