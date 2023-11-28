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
