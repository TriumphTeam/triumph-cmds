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

import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.triumphteam.cmd.core.extension.CommandOptions
import dev.triumphteam.cmd.core.extension.SuggestionMapper
import dev.triumphteam.cmd.core.extension.sender.SenderExtension
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod
import dev.triumphteam.cmd.discord.NsfwProcessor
import dev.triumphteam.cmd.discord.annotation.NSFW
import dev.triumphteam.cmds.kord.sender.Sender
import dev.triumphteam.cmds.useCoroutines

public class KordCommandOptions<S>(
    senderExtension: SenderExtension<Sender, S>,
    builder: Builder<S>,
) : CommandOptions<Sender, S, KordCommandOptions<S>, Choice>(senderExtension, builder) {

    public class Builder<S>(kord: Kord) :
        CommandOptions.Builder<Sender, S, KordCommandOptions<S>, Builder<S>, Choice>() {

        init {
            // Setters have to be done first thing, so they can be overridden.
            extensions { extension ->
                extension.useCoroutines(
                    coroutineScope = kord,
                    coroutineContext = kord.coroutineContext,
                    validateOptionals = false,
                    validateLimitless = false,
                )
                extension.setSuggestionMapper(KordSuggestionMapper())
                extension.addAnnotationProcessor(NSFW::class.java, NsfwProcessor())
            }
        }

        override fun getThis(): Builder<S> = this

        internal fun build(senderExtension: SenderExtension<Sender, S>): KordCommandOptions<S> {
            return KordCommandOptions(senderExtension, this)
        }
    }

    private class KordSuggestionMapper : SuggestionMapper<Choice> {

        override fun map(values: List<String>, type: Class<*>): List<Choice> {
            return values.mapToChoices(type)
        }

        override fun mapBackwards(values: List<Choice>): List<String> {
            return values.map { it.value.toString() }
        }

        override fun filter(
            input: String,
            values: List<Choice>,
            method: SuggestionMethod,
        ): List<Choice> {
            return when (method) {
                SuggestionMethod.STARTS_WITH -> values.filter { it.name.lowercase().startsWith(input.lowercase()) }
                SuggestionMethod.CONTAINS -> values.filter { input.lowercase() in it.name.lowercase() }
                else -> values
            }
        }

        override fun getType(): Class<*> = Choice::class.java

        private fun List<String>.mapToChoices(type: Class<*>): List<Choice> {
            val kordType = type.kordType
            // TODO(important): Make the limit a shared constant
            val sequence = asSequence().take(25)

            return when (kordType) {
                is ApplicationCommandOptionType.Number -> {
                    sequence
                        .mapNotNull(String::toDoubleOrNull)
                        .map { Choice.NumberChoice(it.toString(), Optional.Missing(), it.toDouble()) }
                        .toList()
                }

                is ApplicationCommandOptionType.Integer -> {
                    sequence
                        .mapNotNull(String::toLongOrNull)
                        .map { Choice.IntegerChoice(it.toString(), Optional.Missing(), it) }
                        .toList()
                }

                else -> {
                    sequence.map { Choice.StringChoice(it, Optional.Missing(), it) }.toList()
                }
            }
        }
    }

}
