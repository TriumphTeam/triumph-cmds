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
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic implementation of a sender for this JDA implementation.
 */
final class PrefixedCommandSender implements PrefixedSender {

    private final Message message;
    private final User user;
    private final Member member;
    private final TextChannel channel;
    private final Guild guild;
    private final JDA jda;

    public PrefixedCommandSender(final @NotNull Message message) {
        this.message = message;
        this.user = message.getAuthor();
        this.member = message.getMember();
        this.channel = message.getChannel().asTextChannel();
        this.guild = message.getGuild();
        this.jda = message.getJDA();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Message getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull User getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Member getMember() {
        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull TextChannel getChannel() {
        return channel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Guild getGuild() {
        return guild;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull JDA getJDA() {
        return jda;
    }
}
