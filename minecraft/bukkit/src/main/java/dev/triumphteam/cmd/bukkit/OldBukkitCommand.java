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

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.annotation.AnnotationContainer;
import dev.triumphteam.cmd.core.command.Command;
import dev.triumphteam.cmd.core.command.ParentCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class OldBukkitCommand<S> extends org.bukkit.command.Command implements ParentCommand<S, BukkitSubCommand<S>> {

    private final MessageRegistry<S> messageRegistry;

    private final SenderMapper<CommandSender, S> senderMapper;

    private final Map<String, BukkitSubCommand<S>> subCommands = new HashMap<>();
    private final Map<String, BukkitSubCommand<S>> subCommandAliases = new HashMap<>();

    public OldBukkitCommand(final @NotNull String name, final @NotNull BukkitCommandProcessor<S> processor) {
        super(name);

        this.description = processor.getDescription();
        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderMapper = processor.getSenderMapper();
    }

    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull BukkitSubCommand<S> subCommand) {
        subCommands.putIfAbsent(name, subCommand);
    }

    @Override
    public void addSubCommandAlias(final @NotNull String alias, final @NotNull BukkitSubCommand<S> subCommand) {
        subCommandAliases.putIfAbsent(alias, subCommand);
    }

    /**
     * {@inheritDoc}
     * @throws CommandExecutionException If the sender mapper returns null.
     */
    @Override
    public boolean execute(
            final @NotNull CommandSender sender,
            final @NotNull String commandLabel,
            final @NotNull String @NotNull [] args
    ) {
        /*BukkitSubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        final S mappedSender = senderMapper.map(sender);
        if (mappedSender == null) {
            throw new CommandExecutionException("Invalid sender. Sender mapper returned null");
        }

        if (subCommand == null || (args.length > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(getName(), subCommandName));
            return true;
        }

        final CommandPermission permission = subCommand.getPermission();
        if (!CommandPermission.hasPermission(sender, permission)) {
            messageRegistry.sendMessage(BukkitMessageKey.NO_PERMISSION, mappedSender, new NoPermissionMessageContext(getName(), subCommand.getName(), permission));
            return true;
        }

        final List<String> commandArgs = Arrays.asList(!subCommand.isDefault() ? Arrays.copyOfRange(args, 1, args.length) : args);

        subCommand.execute(mappedSender, commandArgs);*/
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String @NotNull [] args) throws IllegalArgumentException {
        if (args.length == 0) return emptyList();
        return emptyList();
        /*BukkitSubCommand<S> subCommand = getDefaultSubCommand();

        final String arg = args[0].toLowerCase();

        if (args.length == 1 && (subCommand == null || !subCommand.hasArguments())) {
            return subCommands.entrySet().stream()
                    .filter(it -> !it.getValue().isDefault())
                    .filter(it -> it.getKey().startsWith(arg))
                    .filter(it -> {
                        final CommandPermission permission = it.getValue().getPermission();
                        return CommandPermission.hasPermission(sender, permission);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        if (subCommandExists(arg)) subCommand = getSubCommand(arg);
        if (subCommand == null) return emptyList();

        final CommandPermission permission = subCommand.getPermission();
        if (!CommandPermission.hasPermission(sender, permission)) return emptyList();

        final S mappedSender = senderMapper.map(sender);
        if (mappedSender == null) {
            return emptyList();
        }

        final List<String> commandArgs = Arrays.asList(args);
        return subCommand.getSuggestions(mappedSender, !subCommand.isDefault() ? commandArgs.subList(1, commandArgs.size()) : commandArgs);*/
    }

    @Override
    public @NotNull AnnotationContainer getAnnotations() {
        return null;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommands() {
        return null;
    }

    @Override
    public @NotNull Map<String, Command<S>> getCommandAliases() {
        return null;
    }

    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull Command<S> subCommand, final boolean isAlias) {

    }
}
