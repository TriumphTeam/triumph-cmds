package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.cmds.bukkit.factory.BukkitSubCommandFactory;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.command.Command;
import dev.triumphteam.core.command.SimpleSubCommand;
import dev.triumphteam.core.command.SubCommandHolder;
import dev.triumphteam.core.registry.ArgumentRegistry;
import dev.triumphteam.core.registry.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class BukkitCommand extends org.bukkit.command.Command implements Command {

    private final ArgumentRegistry<CommandSender> argumentRegistry;
    private final RequirementRegistry<CommandSender> requirementRegistry;

    private final String name;
    private final List<String> alias;

    private final Map<String, SubCommandHolder<CommandSender>> holders = new HashMap<>();
    private final Map<String, SubCommandHolder<CommandSender>> aliases = new HashMap<>();

    public BukkitCommand(
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        super(name);
        setAliases(alias);

        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;

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

            final SimpleSubCommand<CommandSender> subCommand = new BukkitSubCommandFactory(baseCommand, method, argumentRegistry, requirementRegistry).create();
            if (subCommand == null) continue;
            added = true;

            // TODO add this later and add aliases
            final String subCommandName = subCommand.getName();
            final List<String> subCommandAlias = subCommand.getAlias();

            final SubCommandHolder<CommandSender> holder = holders.get(subCommandName);
            if (holder == null) {
                final SubCommandHolder<CommandSender> newHolder = new SubCommandHolder<>(subCommand);
                holders.put(subCommandName, newHolder);
                for (final String alias : subCommandAlias) {
                    aliases.put(alias, newHolder);
                }
                continue;
            }

            holder.add(subCommand);
            // TODO handle alias later

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
        SubCommandHolder<CommandSender> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.length > 0) subCommandName = args[0].toLowerCase();

        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        if (subCommand == null) {
            sender.sendMessage("Command doesn't exist matey.");
            return true;
        }

        final List<String> commandArgs = new LinkedList<>();
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
    @NotNull
    @Override
    public String getName() {
        return name;
    }

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
    private SubCommandHolder<CommandSender> getDefaultSubCommand() {
        return holders.get(Default.DEFAULT_CMD_NAME);
    }

    @Nullable
    private SubCommandHolder<CommandSender> getSubCommand(@NotNull final String key) {
        final SubCommandHolder<CommandSender> holder = holders.get(key);
        if (holder != null) return holder;
        return aliases.get(key);
    }

    private boolean subCommandExists(@NotNull final String key) {
        return holders.containsKey(key) || aliases.containsKey(key);
    }

}
