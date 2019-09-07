package me.mattstudios.mf.components;

import org.bukkit.command.CommandSender;

@FunctionalInterface
interface MessageResolver {

    /**
     * Resolves messages and executes the code registered in it.
     *
     * @param commandSender The command sender to send the message to.
     */
    void resolve(CommandSender commandSender);

}
