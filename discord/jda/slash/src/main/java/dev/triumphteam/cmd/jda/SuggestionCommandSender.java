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
package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.jda.sender.SlashSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class SuggestionCommandSender implements SlashSender {

    private final CommandAutoCompleteInteractionEvent event;

    public SuggestionCommandSender(final @NotNull CommandAutoCompleteInteractionEvent event) {
        this.event = event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Guild getGuild() {
        return event.getGuild();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable MessageChannelUnion getChannel() {
        return event.getChannel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull User getUser() {
        return event.getUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Member getMember() {
        return event.getMember();
    }

    @Override
    public @NotNull ReplyCallbackAction reply(final @NotNull String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ReplyCallbackAction reply(final @NotNull MessageCreateData message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ReplyCallbackAction reply(final @NotNull MessageEmbed embed, final @NotNull MessageEmbed @NotNull ... embeds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ReplyCallbackAction reply(final @NotNull Collection<? extends MessageEmbed> embeds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ReplyCallbackAction deferReply() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ReplyCallbackAction deferReply(final boolean ephemeral) {
        throw new UnsupportedOperationException();
    }
}
