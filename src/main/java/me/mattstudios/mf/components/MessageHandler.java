package me.mattstudios.mf.components;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

import static me.mattstudios.mf.components.Util.color;

public class MessageHandler {

    // The map with the messages to send.
    private Map<Message, MessageResolver> messages = new HashMap<>();

    // Registers all the default messages.
    public MessageHandler() {
        register(Message.NO_PERMISSION, (sender, arg) -> sender.sendMessage(color("&cYou don't have permission to execute this command!")));
        register(Message.NO_CONSOLE, (sender, arg) -> sender.sendMessage(color("&cCommand can't be executed through the console!")));
        register(Message.DOESNT_EXISTS, (sender, arg) -> sender.sendMessage(color("&cThe command you're trying to use doesn't exist!")));
        register(Message.WRONG_USAGE, (sender, arg) -> sender.sendMessage(color("&cWrong usage for the command!")));
        register(Message.MUST_BE_NUMBER, (sender, arg) -> sender.sendMessage(color("&c" + arg + " must be a number!")));
        register(Message.MUST_BE_NUMBER_DECIMAL, (sender, arg) -> sender.sendMessage(color("&c" + arg + " must be a number!")));
        register(Message.MUST_BE_PLAYER, (sender, arg) -> sender.sendMessage(color("&c" + arg + " is not a valid player!")));
        register(Message.INVALID_VALUE, (sender, arg) -> sender.sendMessage(color("&c" + arg + " is invalid!")));
    }

    /**
     * Method to register new messages and overwrite the existing ones.
     *
     * @param message         The message ENUM to be set (might change to string later).
     * @param messageResolver The message resolver function.
     */
    public void register(Message message, MessageResolver messageResolver) {
        messages.put(message, messageResolver);
    }

    /**
     * Sends the registered message to the command sender.
     *
     * @param message       The message ENUM.
     * @param commandSender The command sender to send the message to.
     */
    public void sendMessage(Message message, CommandSender commandSender, String argument) {
        messages.get(message).resolve(commandSender, argument);
    }

}
