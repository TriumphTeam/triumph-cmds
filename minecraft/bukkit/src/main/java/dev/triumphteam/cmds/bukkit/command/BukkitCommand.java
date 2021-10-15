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
package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.cmds.bukkit.factory.BukkitSubCommandFactory;
import dev.triumphteam.cmds.core.BaseCommand;
import dev.triumphteam.cmds.core.Command;
import dev.triumphteam.cmds.core.SimpleSubCommand;
import dev.triumphteam.cmds.core.SubCommand;
import dev.triumphteam.cmds.core.annotations.Default;
import dev.triumphteam.cmds.core.argument.ArgumentRegistry;
import dev.triumphteam.cmds.core.message.MessageRegistry;
import dev.triumphteam.cmds.core.requirement.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BukkitCommand extends org.bukkit.command.Command implements Command {

    private final ArgumentRegistry<CommandSender> argumentRegistry;
    private final RequirementRegistry<CommandSender> requirementRegistry;
    private final MessageRegistry<CommandSender> messageRegistry;

    private final String name;
    private final List<String> alias;

    private final Map<String, SubCommand<CommandSender>> holders = new HashMap<>();
    private final Map<String, SubCommand<CommandSender>> aliases = new HashMap<>();

    public BukkitCommand(
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry,
            @NotNull final MessageRegistry<CommandSender> messageRegistry
    ) {
        super(name);
        setAliases(alias);

        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
        this.messageRegistry = messageRegistry;

        this.name = name;
        this.alias = alias;
    }

    @Override
    public boolean addSubCommands(@NotNull final BaseCommand baseCommand) {
        boolean added = false;
        final Method[] methods = baseCommand.getClass().getDeclaredMethods();
        //methods.sort(Comparator.comparing(Method::getName));
        for (final Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final SimpleSubCommand<CommandSender> subCommand = new BukkitSubCommandFactory(baseCommand, method, argumentRegistry, requirementRegistry, messageRegistry).create(name);
            if (subCommand == null) continue;
            added = true;

            // TODO add aliases
            final String subCommandName = subCommand.getName();
            final List<String> subCommandAlias = subCommand.getAlias();

            final SubCommand<CommandSender> holder = holders.get(subCommandName);
            // FIXME: 9/2/2021 Add message registry and fix holder removal
        }

        return added;
    }

    @Override
    public boolean execute(
            @NotNull final CommandSender sender,
            @NotNull final String commandLabel,
            @NotNull final String[] args
    ) {
        // TODO DEBUG
        final double start = System.nanoTime();
        // // //
        SubCommand<CommandSender> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();

        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        if (subCommand == null) {
            sender.sendMessage("Command doesn't exist matey.");
            return true;
        }

        final List<String> commandArgs = new ArrayList<>();
        Collections.addAll(commandArgs, args);
        subCommand.execute(sender, commandArgs);

        final double end = System.nanoTime();
        // TODO DEBUG
        final DecimalFormat format = new DecimalFormat("#.####");
        System.out.println("Command (`" + name + "`) execution took: " + format.format((end - start) / 1_000_000.0) + "ms.");
        // // //
        return true;
    }

    // TODO comments in this class

    /**
     * Used to get Command {@link #name} of this particular Command.
     *
     * @return The command name.
     */
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * Used to get the {@link #alias} for this particular Command
     *
     * @return The command name.
     */
    @NotNull
    @Override
    public List<String> getAlias() {
        return alias;
    }

    /**
     * Gets a default command if present.
     *
     * @return A default SubCommand.
     */
    @Nullable
    private SubCommand<CommandSender> getDefaultSubCommand() {
        return holders.get(Default.DEFAULT_CMD_NAME);
    }

    /**
     * Used in order to search for the given {@link SubCommand<CommandSender>} in the {@link #aliases}
     *
     * @param key the String to look for the {@link SubCommand<CommandSender>}
     * @return the {@link SubCommand<CommandSender>} for the particular key or NULL
     */
    @Nullable
    private SubCommand<CommandSender> getSubCommand(@NotNull final String key) {
        final SubCommand<CommandSender> subCommand = holders.get(key);
        if (subCommand != null) return subCommand;
        return aliases.get(key);
    }

    private boolean subCommandExists(@NotNull final String key) {
        return holders.containsKey(key) || aliases.containsKey(key);
    }

}
