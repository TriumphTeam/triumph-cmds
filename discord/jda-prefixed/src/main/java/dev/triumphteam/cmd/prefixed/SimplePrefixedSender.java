package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimplePrefixedSender implements PrefixedSender {

    private final Message message;
    private final User user;
    private final Member member;
    private final TextChannel channel;
    private final Guild guild;

    public SimplePrefixedSender(
            @NotNull final Message message,
            @NotNull final User user,
            @NotNull final TextChannel channel
    ) {
        this.message = message;
        this.user = user;
        this.member = message.getMember();
        this.channel = channel;
        this.guild = channel.getGuild();
    }

    @NotNull
    @Override
    public Message getMessage() {
        return message;
    }

    @NotNull
    @Override
    public User getUser() {
        return user;
    }

    @Nullable
    @Override
    public Member getMember() {
        return member;
    }

    @NotNull
    @Override
    public TextChannel getChannel() {
        return channel;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return guild;
    }

}
