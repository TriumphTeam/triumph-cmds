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
package dev.triumphteam.cmd.discord;

import dev.triumphteam.cmd.core.command.InternalBranchCommand;
import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import dev.triumphteam.cmd.core.command.InternalParentCommand;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.Map;
import java.util.function.Supplier;

public final class CommandWalkUtil {

    public static <D, S, ST> @Nullable LeafResult<D, S, ST> findExecutable(
            final @NotNull S sender,
            final @NotNull Map<String, InternalRootCommand<D, S, ST>> commands,
            final @NotNull Deque<String> commandPath,
            final boolean sendMessage
    ) {
        // Immediately pop first to get root.
        InternalParentCommand<D, S, ST> parentCommand = commands.get(commandPath.pop());
        Supplier<Object> instanceSupplier = null;
       do {
            // Find command with this name;
            final InternalCommand<D, S, ST> command = parentCommand.findCommand(sender, commandPath, sendMessage);
            if (command == null) return null;

            if (command instanceof InternalLeafCommand) {
                return new LeafResult<>((InternalLeafCommand<D, S, ST>) command, instanceSupplier);
            }

            if (!(command instanceof InternalBranchCommand)) {
                throw new IllegalStateException("Command should be either a branch or leaf command.");
            }

            final InternalBranchCommand<D, S, ST> branchCommand = (InternalBranchCommand<D, S, ST>) command;

            if (branchCommand.hasArguments()) {
                throw new IllegalStateException("Argument as sub commands are not allowed on Discord.");
            }

            parentCommand = branchCommand;
            final Supplier<Object> finalInstanceSupplier = instanceSupplier;
            instanceSupplier = () -> branchCommand.createInstance(finalInstanceSupplier);
        } while (!commandPath.isEmpty());

        return null;
    }
}
