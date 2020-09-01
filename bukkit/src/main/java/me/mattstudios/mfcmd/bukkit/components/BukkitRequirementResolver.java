package me.mattstudios.mfcmd.bukkit.components;

import me.mattstudios.mfcmd.base.components.RequirementResolver;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface BukkitRequirementResolver extends RequirementResolver<CommandSender> {

    @Override
    boolean resolve(CommandSender sender);

}
