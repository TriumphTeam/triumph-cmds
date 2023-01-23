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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class BukkitCommand<S> extends Command {
    protected BukkitCommand(@NotNull final String name, @NotNull final String description, @NotNull final String usageMessage, @NotNull final List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
        return false;
    }

    /*private final Map<String, ExecutableCommand<S>> commands = new HashMap<>();

    private final MessageRegistry<S> messageRegistry;
    private final String syntax;

    private final CommandMeta meta;
    private ExecutableCommand<S> parentCommandWithArgument;
    private SenderExtension<CommandSender, S> senderExtension;

    BukkitCommand(
            final @NotNull RootCommandProcessor<CommandSender, S> processor,
            final @NotNull MessageRegistry<S> messageRegistry
    ) {
        super(processor.getName());

        this.messageRegistry = messageRegistry;
        this.meta = processor.createMeta();
        this.senderExtension = processor.getCommandOptions().getSenderExtension();

        this.syntax = "/" + getName();
    }

    @Override
    public boolean execute(
            @NotNull final CommandSender sender,
            @NotNull final String commandLabel,
            @NotNull final String[] args
    ) {
        final List<String> arguments = Arrays.asList(args);

        // TODO COMPOSITION OVER INHERITANCE TO REDUCE REPETITION

        final String commandName = nameFromArguments(arguments);
        final ExecutableCommand<S> subCommand = getSubCommand(commandName, arguments.size());

        final S mappedSender = senderExtension.map(sender);

        if (subCommand == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new InvalidCommandContext(meta, commandName));
            return true;
        }

        // Executing the subcommand.
        try {
            subCommand.execute(
                    mappedSender,
                    commandName,
                    null,
                    !subCommand.isDefault() ? arguments.subList(1, arguments.size()) : arguments
            );
        } catch (final @NotNull Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command")
                    .initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }

        return true;
    }

    @Override
    public @NotNull String getSyntax() {
        return syntax;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommands() {
        return commands;
    }

    @Override
    public @NotNull Map<String, ExecutableCommand<S>> getCommandAliases() {
        return commands;
    }

    @Override
    public @Nullable ExecutableCommand<S> getParentCommandWithArgument() {
        return parentCommandWithArgument;
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }*/
}
