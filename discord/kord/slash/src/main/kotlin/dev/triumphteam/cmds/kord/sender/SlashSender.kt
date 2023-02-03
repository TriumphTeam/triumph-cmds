package dev.triumphteam.cmds.kord.sender

public interface SlashSender {

    public suspend fun reply(message: String)
}
