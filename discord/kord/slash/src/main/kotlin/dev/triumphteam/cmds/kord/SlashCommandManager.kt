package dev.triumphteam.cmds.kord

import dev.kord.core.Kord
import dev.triumphteam.cmd.core.CommandManager
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer
import dev.triumphteam.cmds.kord.sender.SlashSender

public class SlashCommandManager<S>(
    private val kord: Kord,
    commandOptions: SlashCommandOptions<S>,
) : CommandManager<SlashSender, S, SlashCommandOptions<S>>(commandOptions) {

    override fun registerCommand(command: Any) {
        TODO("Not yet implemented")
    }

    override fun unregisterCommand(command: Any) {
        TODO("Not yet implemented")
    }

    override fun getRegistryContainer(): RegistryContainer<SlashSender, S> {
        TODO("Not yet implemented")
    }
}
