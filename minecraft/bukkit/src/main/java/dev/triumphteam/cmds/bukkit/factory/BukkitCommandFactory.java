package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractCommandFactory;
import dev.triumphteam.core.registry.ArgumentRegistry;
import dev.triumphteam.core.registry.RequirementRegistry;
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
     * Factory method for creating a new {@link BukkitCommand}.
     *
     * @param baseCommand The {@link BaseCommand} to pass to the factory to retrieve the annotation data.
     * @return A new {@link BukkitCommand}.
     */
    public static BukkitCommand createFrom(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        return new BukkitCommandFactory(baseCommand, argumentRegistry, requirementRegistry).create();
    }

    /**
     * Creates the final {@link BukkitCommand}.
     *
     * @return A new {@link BukkitCommand}.
     */
    @NotNull
    @Override
    protected BukkitCommand create() {
        return new BukkitCommand(getName(), getAlias(), argumentRegistry, requirementRegistry);
    }

}
