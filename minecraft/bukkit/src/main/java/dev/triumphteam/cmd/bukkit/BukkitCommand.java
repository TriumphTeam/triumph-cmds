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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class BukkitCommand<S> extends org.bukkit.command.Command implements Command<S, BukkitSubCommand<S>> {

    private final MessageRegistry<S> messageRegistry;

    private final SenderMapper<CommandSender, S> senderMapper;

    private final Map<String, BukkitSubCommand<S>> subCommands = new HashMap<>();
    private final Map<String, BukkitSubCommand<S>> subCommandAliases = new HashMap<>();

    public BukkitCommand(@NotNull final String name, @NotNull final BukkitCommandProcessor<S> processor) {
        super(name);

        this.description = processor.getDescription();
        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderMapper = processor.getSenderMapper();
    }

    @Override
    public void addSubCommand(@NotNull final String name, @NotNull final BukkitSubCommand<S> subCommand) {
        subCommands.putIfAbsent(name, subCommand);
    }

    @Override
    public void addSubCommandAlias(@NotNull final String alias, @NotNull final BukkitSubCommand<S> subCommand) {
        subCommandAliases.putIfAbsent(alias, subCommand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
        BukkitSubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        final S mappedSender = senderMapper.map(sender);

        if (subCommand == null || (args.length > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(getName(), subCommandName));
            return true;
        }

        final CommandPermission permission = subCommand.getPermission();
        if (permission != null && !permission.hasPermission(sender)) {
            messageRegistry.sendMessage(BukkitMessageKey.NO_PERMISSION, mappedSender, new NoPermissionMessageContext(getName(), subCommand.getName(), permission));
            return true;
        }

        final List<String> commandArgs = Arrays.asList(!subCommand.isDefault() ? Arrays.copyOfRange(args, 1, args.length) : args);

        subCommand.execute(mappedSender, commandArgs);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, @NotNull final String[] args) throws IllegalArgumentException {
        if (args.length == 0) return emptyList();
        BukkitSubCommand<S> subCommand = getDefaultSubCommand();

        final String arg = args[0].toLowerCase();

        if (args.length == 1 && (subCommand == null || !subCommand.hasArguments())) {
            return subCommands.entrySet().stream()
                    .filter(it -> !it.getValue().isDefault())
                    .filter(it -> it.getKey().startsWith(arg))
                    .filter(it -> {
                        final CommandPermission permission = it.getValue().getPermission();
                        if (permission == null) return false;
                        return permission.hasPermission(sender);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        if (subCommandExists(arg)) subCommand = getSubCommand(arg);
        if (subCommand == null) return emptyList();

        final CommandPermission permission = subCommand.getPermission();
        if (permission != null && permission.hasPermission(sender)) return emptyList();

        final S mappedSender = senderMapper.map(sender);

        final List<String> commandArgs = Arrays.asList(args);
        return subCommand.getSuggestions(mappedSender, !subCommand.isDefault() ? commandArgs.subList(1, commandArgs.size()) : commandArgs);
    }

    /**
     * Gets a default command if present.
     *
     * @return A default SubCommand.
     */
    @Nullable
    private BukkitSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Used in order to search for the given {@link SubCommand<CommandSender>} in the {@link #subCommandAliases}
     *
     * @param key the String to look for the {@link SubCommand<CommandSender>}
     * @return the {@link SubCommand<CommandSender>} for the particular key or NULL
     */
    @Nullable
    private BukkitSubCommand<S> getSubCommand(@NotNull final String key) {
        final BukkitSubCommand<S> subCommand = subCommands.get(key);
        if (subCommand != null) return subCommand;
        return subCommandAliases.get(key);
    }

    /**
     * Checks if a SubCommand with the specified key exists.
     *
     * @param key the Key to check for
     * @return whether a SubCommand with that key exists
     */
    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key) || subCommandAliases.containsKey(key);
    }
}
