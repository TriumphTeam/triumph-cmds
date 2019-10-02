/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.mattstudios.mf.base;

import me.mattstudios.mf.base.components.MessageResolver;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

import static me.mattstudios.mf.base.components.Util.color;

@SuppressWarnings("WeakerAccess")
public class MessageHandler {

    // The map with the messages to send.
    private Map<String, MessageResolver> messages = new HashMap<>();

    // Registers all the default messages.
    MessageHandler() {
        register("cmd.no.permission", (sender, arg) -> sender.sendMessage(color("&cYou don't have permission to execute this command!")));
        register("cmd.no.console", (sender, arg) -> sender.sendMessage(color("&cCommand can't be executed through the console!")));
        register("cmd.no.exists", (sender, arg) -> sender.sendMessage(color("&cThe command you're trying to use doesn't exist!")));
        register("cmd.wrong.usage", (sender, arg) -> sender.sendMessage(color("&cWrong usage for the command!")));
        register("arg.must.be.number", (sender, arg) -> sender.sendMessage(color("&c" + arg + " must be a number!")));
        register("arg.must.be.number.decimal", (sender, arg) -> sender.sendMessage(color("&c" + arg + " must be a number!")));
        register("arg.must.be.player", (sender, arg) -> sender.sendMessage(color("&c" + arg + " is not a valid player!")));
        register("arg.invalid.value", (sender, arg) -> sender.sendMessage(color("&c" + arg + " is invalid!")));
    }

    /**
     * Method to register new messages and overwrite the existing ones.
     *
     * @param messageId         The message ID to be set.
     * @param messageResolver The message resolver function.
     */
    public void register(String messageId, MessageResolver messageResolver) {
        messages.put(messageId, messageResolver);
    }

    /**
     * Sends the registered message to the command sender.
     *
     * @param messageId       The message ID.
     * @param commandSender The command sender to send the message to.
     * @param argument      The argument to pass down.
     */
    void sendMessage(String messageId, CommandSender commandSender, String argument) {
        messages.get(messageId).resolve(commandSender, argument);
    }

}
