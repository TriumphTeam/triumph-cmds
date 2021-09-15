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
package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.cmds.core.BaseCommand;
import dev.triumphteam.cmds.core.argument.ArgumentRegistry;
import dev.triumphteam.cmds.core.factory.AbstractCommandFactory;
import dev.triumphteam.cmds.core.message.MessageRegistry;
import dev.triumphteam.cmds.core.requirement.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandFactory extends AbstractCommandFactory<BukkitCommand> {

    // TODO probably move this lol
    private final ArgumentRegistry<CommandSender> argumentRegistry;
    private final RequirementRegistry<CommandSender> requirementRegistry;
    private final MessageRegistry<CommandSender> messageRegistry;

    public BukkitCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry,
            @NotNull final MessageRegistry<CommandSender> messageRegistry
    ) {
        super(baseCommand);
        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
        this.messageRegistry = messageRegistry;
    }

    /**
     * Creates the final {@link BukkitCommand}.
     *
     * @return A new {@link BukkitCommand}.
     */
    @NotNull
    @Override
    public BukkitCommand create() {
        return new BukkitCommand(getName(), getAlias(), argumentRegistry, requirementRegistry, messageRegistry);
    }

}
