package dev.triumphteam.cmd.core.extention.command;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Settings<D, S> {

    /**
     * Tests all the requirements present in the setting.
     * If any fails, handle the return and returns false.
     * Only truly succeeds if all requirements pass.
     *
     * @param sender          The sender to test the requirements to.
     * @param meta            The {@link CommandMeta} which is used by some requirements.
     * @param senderMapper    The {@link SenderMapper} which is used by some requirements.
     * @return True only if all requirements pass.
     */
    boolean testRequirements(
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    );

    /**
     * Tests all the requirements present in the setting.
     * If any fails, handle the return and returns false.
     * Only truly succeeds if all requirements pass.
     *
     * @param messageRegistry The {@link MessageRegistry} to send message to the sender.
     * @param sender          The sender to test the requirements to.
     * @param meta            The {@link CommandMeta} which is used by some requirements.
     * @param senderMapper    The {@link SenderMapper} which is used by some requirements.
     * @return True only if all requirements pass.
     */
    boolean testRequirements(
            final @NotNull MessageRegistry<S> messageRegistry,
            final @NotNull S sender,
            final @NotNull CommandMeta meta,
            final @NotNull SenderMapper<D, S> senderMapper
    );

    class Builder<D, S> {

        private final List<Requirement<D, S>> requirements = new ArrayList<>();

        @Contract("_ -> this")
        public @NotNull Builder<D, S> addRequirement(final @NotNull Requirement<D, S> requirement) {
            requirements.add(requirement);
            return this;
        }

        // TODO add more things to the settings

        public Settings<D, S> build() {
            return new ImmutableSettings<>(Collections.unmodifiableList(requirements));
        }
    }
}
