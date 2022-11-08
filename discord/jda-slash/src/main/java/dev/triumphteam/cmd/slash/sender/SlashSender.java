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
package dev.triumphteam.cmd.slash.sender;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Works like a shortcut for most things present on {@link SlashCommandInteractionEvent}.
 * Contains the more useful methods from it, but still allows you to get the original event if more is needed.
 */
public interface SlashSender {

    /**
     * Gets the original event if more options are needed.
     *
     * @return The original event.
     */
    @NotNull SlashCommandInteractionEvent getEvent();

    /**
     * Gets the guild that the command was sent in or null if it was sent in a private message.
     *
     * @return The guild.
     */
    @Nullable Guild getGuild();

    /**
     * Gets the channel that the command was sent in.
     *
     * @return The channel.
     */
    @NotNull MessageChannel getChannel();

    /**
     * Gets the user that sent the command.
     *
     * @return The user.
     */
    @NotNull User getUser();

    /**
     * Gets the member that sent the command or null if the user is not a member.
     *
     * @return The member.
     */
    @Nullable Member getMember();

    /**
     * Gets the interaction hook for the command.
     *
     * @return The interaction hook.
     */
    @NotNull InteractionHook getHook();

    /**
     * Replies to the command with a string message.
     *
     * @param message The message to reply with.
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction reply(final @NotNull String message);

    /**
     * Replies to the command with a message.
     *
     * @param message The message to reply with.
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction reply(final @NotNull MessageCreateData message);

    /**
     * Replies to the command with a message embed.
     *
     * @param embed  The embed to reply with.
     * @param embeds The additional embeds.
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction reply(final @NotNull MessageEmbed embed, final @NotNull MessageEmbed @NotNull ... embeds);

    /**
     * Replies to the command with a message embeds.
     *
     * @param embeds The embeds to reply with.
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction reply(final @NotNull Collection<? extends MessageEmbed> embeds);

    /**
     * Defers the reply to the command.
     *
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction deferReply();

    /**
     * Defers the reply to the command but ephemeral.
     *
     * @param ephemeral Whether the message should be ephemeral.
     * @return The reply action.
     */
    @NotNull ReplyCallbackAction deferReply(final boolean ephemeral);

}
