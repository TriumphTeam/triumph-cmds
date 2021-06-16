package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractCommandFactory;
import dev.triumphteam.core.registry.ArgumentRegistry;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandFactory extends AbstractCommandFactory<BukkitCommand> {

    private final ArgumentRegistry argumentRegistry;

    public BukkitCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry argumentRegistry
    ) {
        super(baseCommand);
        this.argumentRegistry = argumentRegistry;
    }

    /**
     * Factory method for creating a new {@link BukkitCommand}.
     *
     * @param baseCommand The {@link BaseCommand} to pass to the factory to retrieve the annotation data.
     * @return A new {@link BukkitCommand}.
     */
    public static BukkitCommand createFrom(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry argumentRegistry
    ) {
        return new BukkitCommandFactory(baseCommand, argumentRegistry).create();
    }

    /**
     * Creates the final {@link BukkitCommand}.
     *
     * @return A new {@link BukkitCommand}.
     */
    @NotNull
    @Override
    protected BukkitCommand create() {
        return new BukkitCommand(getName(), getAlias(), argumentRegistry);
    }

}
