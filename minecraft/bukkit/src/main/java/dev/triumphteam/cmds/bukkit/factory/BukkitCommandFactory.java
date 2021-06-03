package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractCommandFactory;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandFactory extends AbstractCommandFactory<BukkitCommand> {

    public BukkitCommandFactory(@NotNull final BaseCommand baseCommand) {
        super(baseCommand);
    }

    /**
     * Factory method for creating a new {@link BukkitCommand}.
     *
     * @param baseCommand The {@link BaseCommand} to pass to the factory to retrieve the annotation data.
     * @return A new {@link BukkitCommand}.
     */
    public static BukkitCommand of(@NotNull final BaseCommand baseCommand) {
        return new BukkitCommandFactory(baseCommand).create();
    }

    @NotNull
    @Override
    protected BukkitCommand create() {
        return new BukkitCommand(getName(), getAlias());
    }

}
