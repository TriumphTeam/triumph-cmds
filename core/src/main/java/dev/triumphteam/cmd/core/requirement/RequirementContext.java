package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import org.jetbrains.annotations.NotNull;

public interface RequirementContext<D, S> {

    @NotNull CommandMeta getMeta();

    @NotNull SenderMapper<D, S> getSenderMapper();
}
