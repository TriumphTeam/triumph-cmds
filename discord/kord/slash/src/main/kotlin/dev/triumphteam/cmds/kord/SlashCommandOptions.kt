package dev.triumphteam.cmds.kord

import dev.kord.core.Kord
import dev.triumphteam.cmd.core.extention.CommandOptions
import dev.triumphteam.cmd.core.extention.sender.SenderExtension
import dev.triumphteam.cmd.discord.ChoiceProcessor
import dev.triumphteam.cmd.discord.NsfwProcessor
import dev.triumphteam.cmd.discord.annotation.Choice
import dev.triumphteam.cmd.discord.annotation.NSFW
import dev.triumphteam.cmds.kord.sender.SlashSender
import dev.triumphteam.cmds.useCoroutines

public class SlashCommandOptions<S>(
    senderExtension: SenderExtension<SlashSender, S>,
    builder: Builder<S>,
) : CommandOptions<SlashSender, S>(senderExtension, builder) {

    public class Setup<S>(registryContainer: SlashRegistryContainer<S>) :
        CommandOptions.Setup<SlashSender, S, Setup<S>>(registryContainer)

    public class Builder<S>(
        registryContainer: SlashRegistryContainer<S>,
        kord: Kord,
    ) :
        CommandOptions.Builder<SlashSender, S, SlashCommandOptions<S>, Setup<S>, Builder<S>>(
            Setup(registryContainer)
        ) {

        init {
            // Setters have to be done first thing, so they can be overriden.
            extensions { extension ->
                extension.useCoroutines(coroutineScope = kord, coroutineContext = kord.coroutineContext)
                extension.addAnnotationProcessor(Choice::class.java, ChoiceProcessor(registryContainer.choiceRegistry))
                extension.addAnnotationProcessor(NSFW::class.java, NsfwProcessor())
            }
        }

        override fun build(senderExtension: SenderExtension<SlashSender, S>): SlashCommandOptions<S> {
            return SlashCommandOptions(senderExtension, this)
        }
    }
}
