/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
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
package dev.triumphteam.cmd.prefixed.sender;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default sender for the prefixed JDA platform.
 * Contains all the basic info needed to handle the command.
 * Most of this info can be redundant as they are part of the {@link Message}.
 * I decided to still add them as it can be easier than just getting the message.
 */
public interface PrefixedSender {

    /**
     * Gets the {@link Message} the user sent.
     *
     * @return The original {@link Message}.
     */
    @NotNull
    Message getMessage();

    /**
     * Gets the {@link User} that send the message.
     *
     * @return The {@link User} that sent the message.
     */
    @NotNull
    User getUser();

    /**
     * Gets the {@link Member} that sent the message if the {@link User} is a member.
     * Will be null if the message was not sent by a webhook.
     *
     * @return The {@link Member} that sent the message or null.
     */
    @Nullable
    Member getMember();

    /**
     * Gets the {@link TextChannel} the message was sent on.
     *
     * @return The {@link TextChannel}, will throw exception if the message was not sent in a text channel.
     */
    @NotNull
    TextChannel getChannel();

    /**
     * Gets the {@link Guild} the command was sent from.
     *
     * @return The {@link Guild}.
     */
    @NotNull
    Guild getGuild();

    /**
     * Gets the {@link JDA} instance.
     *
     * @return The {@link JDA} instance.
     */
    @NotNull
    JDA getJDA();

}
