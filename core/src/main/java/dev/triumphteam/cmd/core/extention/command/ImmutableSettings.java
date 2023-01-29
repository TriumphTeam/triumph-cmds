package dev.triumphteam.cmd.core.extention.command;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ImmutableSettings<D, S> implements Settings<D, S> {

    private final List<Requirement<D, S>> requirements;

    public ImmutableSettings(final @NotNull List<Requirement<D, S>> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean testRequirements(
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    ) {
        final Requirement<D, S> requirement = getFailedRequirement(sender, meta, senderMapper);
        if (requirement == null) return true;

        requirement.onDeny(sender, messageRegistry, meta);
        return false;
    }

    @Override
    public boolean testRequirements(
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    ) {
        return getFailedRequirement(sender, meta, senderMapper) == null;
    }

    private @Nullable Requirement<D, S> getFailedRequirement(
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    ) {
        for (final Requirement<D, S> requirement : requirements) {
            if (!requirement.test(sender, meta, senderMapper)) return requirement;
        }

        return null;
    }
}
