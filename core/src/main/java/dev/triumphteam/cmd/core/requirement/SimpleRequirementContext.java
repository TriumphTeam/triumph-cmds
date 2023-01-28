package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import org.jetbrains.annotations.NotNull;

class SimpleRequirementContext<D, S> implements RequirementContext<D, S> {

    private final CommandMeta meta;
    private final SenderMapper<D, S> senderMapper;

    public SimpleRequirementContext(
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    ) {
        this.meta = meta;
        this.senderMapper = senderMapper;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    @Override
    public @NotNull SenderMapper<D, S> getSenderMapper() {
        return senderMapper;
    }
}
