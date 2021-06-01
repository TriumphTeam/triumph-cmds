package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractCommandFactory;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandFactory extends AbstractCommandFactory<BukkitCommand> {

    public BukkitCommandFactory(@NotNull final BaseCommand baseCommand) {
        super(baseCommand);
    }

    @NotNull
    @Override
    public BukkitCommand create() {
        return new BukkitCommand();
    }


}
