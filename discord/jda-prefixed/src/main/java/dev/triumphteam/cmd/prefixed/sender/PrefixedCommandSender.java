package dev.triumphteam.cmd.prefixed.sender;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrefixedCommandSender {

    @NotNull
    Message getMessage();

    @NotNull
    User getUser();

    @Nullable
    Member getMember();

    @NotNull
    TextChannel getChannel();

    @NotNull
    Guild getGuild();

}
