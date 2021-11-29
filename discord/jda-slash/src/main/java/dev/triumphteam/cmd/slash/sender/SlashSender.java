package dev.triumphteam.cmd.slash.sender;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;

public interface SlashSender {

    Guild getGuild();

    RestAction reply(String message);

}
