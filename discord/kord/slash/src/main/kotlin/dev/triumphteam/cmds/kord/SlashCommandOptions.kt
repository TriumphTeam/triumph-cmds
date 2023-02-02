package dev.triumphteam.cmds.kord

import dev.triumphteam.cmd.core.extention.CommandOptions
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer
import dev.triumphteam.cmd.core.extention.sender.SenderExtension
import dev.triumphteam.cmds.kord.sender.SlashSender

public class SlashCommandOptions<S>(
    senderExtension: SenderExtension<SlashSender, S>,
    builder: Builder<S>,
) : CommandOptions<SlashSender, S>(senderExtension, builder) {

    public class Setup<S>(registryContainer: RegistryContainer<SlashSender, S>) :
        CommandOptions.Setup<SlashSender, S, Setup<S>>(registryContainer)

    public class Builder<S>(registryContainer: RegistryContainer<SlashSender, S>) :
        CommandOptions.Builder<SlashSender, S, SlashCommandOptions<S>, Setup<S>, Builder<S>>(
            Setup(registryContainer)
        ) {

        override fun build(senderExtension: SenderExtension<SlashSender, S>): SlashCommandOptions<S> {
            return SlashCommandOptions(senderExtension, this)
        }
    }
}
