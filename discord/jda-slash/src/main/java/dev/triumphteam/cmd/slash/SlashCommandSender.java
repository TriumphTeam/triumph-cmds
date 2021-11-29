package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.slash.sender.SlashSender;
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

final class SlashCommandSender implements SlashSender {

    private final SlashCommandEvent event;

    public SlashCommandSender(@NotNull final SlashCommandEvent event) {
        this.event = event;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public SlashCommandEvent getEvent() {
        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public MessageChannel getChannel() {
        return event.getChannel();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public User getUser() {
        return event.getUser();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Member getMember() {
        return event.getMember();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public InteractionHook getHook() {
        return event.getHook();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction reply(@NotNull final String message) {
        return event.reply(message);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction reply(@NotNull final Message message) {
        return event.reply(message);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction reply(@NotNull final MessageEmbed embed, @NotNull final MessageEmbed... embeds) {
        return event.replyEmbeds(embed, embeds);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction reply(@NotNull final Collection<? extends MessageEmbed> embeds) {
        return event.replyEmbeds(embeds);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction deferReply() {
        return event.deferReply();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyAction deferReply(final boolean ephemeral) {
        return event.deferReply(ephemeral);
    }
}
