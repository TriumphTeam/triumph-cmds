package dev.triumphteam.cmd.slash.sender;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Works like a shortcut for most things present on {@link SlashCommandEvent}.
 * Contains the more useful methods from it, but still allows you to get the original event if more is needed.
 */
public interface SlashSender {

    /**
     * Gets the original event if more options are needed.
     *
     * @return The original event.
     */
    @NotNull
    SlashCommandEvent getEvent();

    /**
     * Gets the guild that the command was sent in or null if it was sent in a private message.
     *
     * @return The guild.
     */
    @Nullable
    Guild getGuild();

    /**
     * Gets the channel that the command was sent in.
     *
     * @return The channel.
     */
    @NotNull
    MessageChannel getChannel();

    /**
     * Gets the user that sent the command.
     *
     * @return The user.
     */
    @NotNull
    User getUser();

    /**
     * Gets the member that sent the command or null if the user is not a member.
     *
     * @return The member.
     */
    @Nullable
    Member getMember();

    /**
     * Gets the interaction hook for the command.
     *
     * @return The interaction hook.
     */
    @NotNull
    InteractionHook getHook();

    /**
     * Replies to the command with a string message.
     *
     * @param message The message to reply with.
     * @return The reply action.
     */
    @NotNull
    ReplyAction reply(@NotNull final String message);

    /**
     * Replies to the command with a message.
     *
     * @param message The message to reply with.
     * @return The reply action.
     */
    @NotNull
    ReplyAction reply(@NotNull final Message message);

    /**
     * Replies to the command with a message embed.
     *
     * @param embed  The embed to reply with.
     * @param embeds The additional embeds.
     * @return The reply action.
     */
    @NotNull
    ReplyAction reply(@NotNull final MessageEmbed embed, @NotNull final MessageEmbed... embeds);

    /**
     * Replies to the command with a message embeds.
     *
     * @param embeds The embeds to reply with.
     * @return The reply action.
     */
    @NotNull
    ReplyAction reply(@NotNull final Collection<? extends MessageEmbed> embeds);

    /**
     * Defers the reply to the command.
     *
     * @return The reply action.
     */
    @NotNull
    ReplyAction deferReply();

    /**
     * Defers the reply to the command but ephemeral.
     *
     * @param ephemeral Whether the message should be ephemeral.
     * @return The reply action.
     */
    @NotNull
    ReplyAction deferReply(boolean ephemeral);

}
