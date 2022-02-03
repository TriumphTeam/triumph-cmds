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
package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class SlashCommandSender implements SlashSender {

    private final SlashCommandInteractionEvent event;

    public SlashCommandSender(@NotNull final SlashCommandInteractionEvent event) {
        this.event = event;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public SlashCommandInteractionEvent getEvent() {
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
    public ReplyCallbackAction reply(@NotNull final String message) {
        return event.reply(message);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyCallbackAction reply(@NotNull final Message message) {
        return event.reply(message);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyCallbackAction reply(@NotNull final MessageEmbed embed, @NotNull final MessageEmbed... embeds) {
        return event.replyEmbeds(embed, embeds);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyCallbackAction reply(@NotNull final Collection<? extends MessageEmbed> embeds) {
        return event.replyEmbeds(embeds);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyCallbackAction deferReply() {
        return event.deferReply();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ReplyCallbackAction deferReply(final boolean ephemeral) {
        return event.deferReply(ephemeral);
    }
}
