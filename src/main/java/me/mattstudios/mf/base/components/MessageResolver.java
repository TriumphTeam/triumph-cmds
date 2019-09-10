package me.mattstudios.mf.base.components;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface MessageResolver {

    /**
     * Resolves messages and executes the code registered in it.
     *
     * @param commandSender The command sender to send the message to.
     * @param argument      The argument to resolve for.
     */
    void resolve(CommandSender commandSender, String argument);

}
