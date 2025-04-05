/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
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

    @NotNull List<Requirement<D, S>> getRequirements();

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
