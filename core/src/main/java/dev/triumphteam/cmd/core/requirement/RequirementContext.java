package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.command.Command;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import org.jetbrains.annotations.NotNull;

/**
 * Holder for some context data to pass over to the {@link RequirementResolver}.
 *
 * @param <D> The default sender.
 * @param <S> The final sender.
 */
public interface RequirementContext<D, S> {

    /**
     * @return The {@link CommandMeta} of the current {@link Command}.
     */
    @NotNull CommandMeta getMeta();

    /**
     * @return The {@link SenderMapper} for reverse mapping if needed.
     */
    @NotNull SenderMapper<D, S> getSenderMapper();
}
