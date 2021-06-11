package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.cmds.bukkit.factory.BukkitSubCommandFactory;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.Command;
import dev.triumphteam.core.registry.ArgumentRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BukkitCommand extends org.bukkit.command.Command implements Command {

    private final ArgumentRegistry argumentRegistry;

    private final String name;
    private final List<String> alias;

    private final Map<String, BukkitSubCommand> subCommands = new HashMap<>();
    private final Map<String, BukkitSubCommand> aliases = new HashMap<>();

    public BukkitCommand(
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final ArgumentRegistry argumentRegistry
    ) {
        super(name);
        setAliases(alias);

        this.argumentRegistry = argumentRegistry;

        this.name = name;
        this.alias = alias;
    }

    @Override
    public boolean addSubCommands(@NotNull final BaseCommand baseCommand) {
        boolean added = false;

        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;

            final BukkitSubCommand subCommand = BukkitSubCommandFactory.of(method, argumentRegistry);
            if (subCommand == null) continue;

            // TODO add this later and add aliases
            subCommands.put(subCommand.name(), subCommand);
            added = true;
        }

        return added;
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
        System.out.println(Arrays.toString(args));
        System.out.println(commandLabel);
        return true;
    }

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

}
