/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitSubCommand<S> extends AbstractSubCommand<S> {

    private final List<Requirement<CommandSender, ?>> defaultRequirements;

    public BukkitSubCommand(
            @NotNull final BukkitSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);

        this.defaultRequirements = processor.getDefaultRequirements();
    }

    public boolean meetsDefaultRequirements(@NotNull final CommandSender defaultSender, @NotNull final S sender) {
        for (final Requirement<CommandSender, ?> requirement : defaultRequirements) {
            if (!requirement.isMet(defaultSender)) {
                requirement.sendMessage(getMessageRegistry(), sender, getParentName(), getName());
                return false;
            }
        }

        return true;
    }

}
