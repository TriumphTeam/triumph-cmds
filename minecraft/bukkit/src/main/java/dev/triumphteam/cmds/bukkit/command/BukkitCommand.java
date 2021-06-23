package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.cmds.bukkit.factory.BukkitSubCommandFactory;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.command.Command;
import dev.triumphteam.core.command.SubCommand;
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

    private final Map<String, BukkitSubCommand> subCommands = new HashMap<>();
    private final Map<String, BukkitSubCommand> aliases = new HashMap<>();

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

        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final BukkitSubCommand subCommand = BukkitSubCommandFactory.createFrom(baseCommand, method, argumentRegistry, requirementRegistry);
            if (subCommand == null) continue;

            // TODO add this later and add aliases
            subCommands.put(subCommand.getName(), subCommand);
            for (final String alias : subCommand.getAlias()) {
                aliases.put(alias, subCommand);
            }
            added = true;
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

        final List<String> commandArgs = new LinkedList<>();
        Collections.addAll(commandArgs, args);
        subCommand.execute(sender, commandArgs);

        final double end = System.nanoTime();
        // TODO DEBUG
        final DecimalFormat format = new DecimalFormat("#.####");
        System.out.println("Command (`" + name + "`, `" + subCommand.getName() + "`) execution took: " + format.format((end - start) / 1_000_000.0) + "ms.");
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
    private SubCommand<CommandSender> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    @Nullable
    private SubCommand<CommandSender> getSubCommand(@NotNull final String key) {
        final SubCommand<CommandSender> subCommand = subCommands.get(key);
        if (subCommand != null) return subCommand;
        return aliases.get(key);
    }

    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key) || aliases.containsKey(key);
    }

}
