package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractCommandFactory;
import dev.triumphteam.core.command.argument.ArgumentRegistry;
import dev.triumphteam.core.command.requirement.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandFactory extends AbstractCommandFactory<BukkitCommand> {

    // TODO probably move this lol
    private final ArgumentRegistry<CommandSender> argumentRegistry;
    private final RequirementRegistry<CommandSender> requirementRegistry;

    public BukkitCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        super(baseCommand);
        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
    }

    /**
     * Creates the final {@link BukkitCommand}.
     *
     * @return A new {@link BukkitCommand}.
     */
    @NotNull
    @Override
    public BukkitCommand create() {
        return new BukkitCommand(getName(), getAlias(), argumentRegistry, requirementRegistry);
    }

}
