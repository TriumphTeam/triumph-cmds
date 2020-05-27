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

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandBase {

    // The map containing the arguments, for error handling
    private final Map<String, String> arguments = new HashMap<>();
    // The mutable list with aliases that can be altered any time
    private final List<String> aliases = new ArrayList<>();
    // The message handler
    private MessageHandler messageHandler;

    // Method that'll run on the registering of the command
    public void onRegister() {
    }

    /**
     * Gets the argument used for the command
     *
     * @param name The argument name
     * @return The argument
     */
    @SuppressWarnings("WeakerAccess")
    public String getArgument(final String name) {
        return arguments.getOrDefault(name, null);
    }

    /**
     * Sends an IDed message to the sender
     *
     * @param messageId The message ID
     * @param sender    The sender
     */
    public void sendMessage(final String messageId, final CommandSender sender) {
        messageHandler.sendMessage(messageId, sender);
    }

    /**
     * Sets the message handler when made
     *
     * @param messageHandler The message handler
     */
    void setMessageHandler(final MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Clears the args
     */
    void clearArgs() {
        arguments.clear();
    }

    /**
     * Adds a new argument
     *
     * @param name     The variable name
     * @param argument The argument
     */
    void addArgument(final String name, final String argument) {
        arguments.put(name, argument);
    }

    /**
     * Gets the list of aliases
     *
     * @return The aliases
     */
    List<String> getAliases() {
        return aliases;
    }

    /**
     * Sets the alias list for the method
     *
     * @param aliases The list new aliases to register
     */
    public void setAliases(final List<String> aliases) {
        this.aliases.addAll(aliases);
    }

}
