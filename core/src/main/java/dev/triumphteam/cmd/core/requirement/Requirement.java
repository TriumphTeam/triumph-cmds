package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import org.jetbrains.annotations.NotNull;

public interface Requirement<D, S> {

    boolean test(
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    );

    void onDeny(
            final @NotNull S sender,
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull CommandMeta meta
    );
}
