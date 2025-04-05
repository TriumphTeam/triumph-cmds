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

    @Override
    public @NotNull List<Requirement<D, S>> getRequirements() {
        return requirements;
    }
}
