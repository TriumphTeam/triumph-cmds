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
package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.minecraft.message.MinecraftMessageKey;
import dev.triumphteam.cmd.minecraft.message.NoPermissionMessageContext;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.service.permission.Subject;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class SpongeCommand<S> implements Command.Raw, dev.triumphteam.cmd.core.Command<S, SpongeSubCommand<S>> {

    private final MessageRegistry<S> messageRegistry;

    private final SenderMapper<Subject, S> senderMapper;

    private final Map<String, SpongeSubCommand<S>> subCommands = new HashMap<>();
    private final Map<String, SpongeSubCommand<S>> subCommandAliases = new HashMap<>();
    private final String description;

    public SpongeCommand(@NotNull final SpongeCommandProcessor<S> processor) {
        RegistryContainer<S> registryContainer = processor.getRegistryContainer();
        description = processor.getDescription();
        this.messageRegistry = registryContainer.getMessageRegistry();
        this.senderMapper = processor.getSenderMapper();
    }

    @Override
    public void addSubCommands(
            @NotNull Map<String, SpongeSubCommand<S>> subCommands,
            @NotNull Map<String, SpongeSubCommand<S>> subCommandAliases
    ) {
        this.subCommands.putAll(subCommands);
        this.subCommandAliases.putAll(subCommandAliases);
    }


    @Override
    public CommandResult process(CommandCause cause,ArgumentReader.Mutable arguments) throws CommandException {
        final String[] args = arguments.totalLength() == 0 ? new String[0] : arguments.input().split(" ");

        SpongeSubCommand<S> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        final S mappedSender = senderMapper.map(cause);

        if (subCommand == null || (args.length > 0 && subCommand.isDefault() && !subCommand.hasArguments())) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, mappedSender, new DefaultMessageContext(cause.identifier(), subCommandName));
            return CommandResult.success();
        }

        final String permission = subCommand.getPermission();
        if (!permission.isEmpty() && !cause.hasPermission(permission)) {
            messageRegistry.sendMessage(MinecraftMessageKey.NO_PERMISSION, mappedSender, new NoPermissionMessageContext(cause.identifier(), subCommand.getName(), permission));
            return CommandResult.success();
        }

        final List<String> commandArgs = Arrays.asList(!subCommand.isDefault() ? Arrays.copyOfRange(args, 1, args.length) : args);

        subCommand.execute(mappedSender, commandArgs);
        return CommandResult.success();
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        String[] args = arguments.input().isEmpty() ? new String[]{""} : arguments.input().split(" ");

        if (args.length == 0) return emptyList();
        SpongeSubCommand<S> subCommand = getDefaultSubCommand();

        final String arg = args[0].toLowerCase();

        if (args.length == 1 && (subCommand == null || !subCommand.hasArguments())) {

            return subCommands.entrySet().stream().filter(it -> !it.getValue().isDefault()).filter(it -> it.getKey().startsWith(arg)).filter(it -> {
                final String permission = it.getValue().getPermission();
                if (permission.isEmpty()) return true;
                return cause.hasPermission(permission);
            }).map(s -> CommandCompletion.of(s.getKey())).collect(Collectors.toList());
        }

        if (subCommandExists(arg)) subCommand = getSubCommand(arg);
        if (subCommand == null) return emptyList();

        final String permission = subCommand.getPermission();
        if (!permission.isEmpty() && !cause.hasPermission(permission)) return emptyList();

        final S mappedSender = senderMapper.map(cause);

        final List<String> commandArgs = Arrays.asList(args);
        return subCommand.getSuggestions(mappedSender, !subCommand.isDefault() ? commandArgs.subList(1, commandArgs.size()) : commandArgs);
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.of(Component.text(description));
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.of(Component.text(description));
    }

    @Override
    public Optional<Component> help(@NonNull CommandCause cause) {
        return Raw.super.help(cause);
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.text(Objects.requireNonNull(getDefaultSubCommand()).getName());
    }


    /**
     * Gets a default command if present.
     *
     * @return A default SubCommand.
     */
    @Nullable
    private SpongeSubCommand<S> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Used in order to search for the given {@link SubCommand<CommandCause>} in the {@link #subCommandAliases}
     *
     * @param key the String to look for the {@link SubCommand<CommandCause>}
     * @return the {@link SubCommand<CommandCause>} for the particular key or NULL
     */
    @Nullable
    private SpongeSubCommand<S> getSubCommand(@NotNull final String key) {
        final SpongeSubCommand<S> subCommand = subCommands.get(key);
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
