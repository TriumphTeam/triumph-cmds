package dev.triumphteam.cmds.kord

import dev.kord.common.entity.Choice
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion
import dev.triumphteam.cmd.core.suggestion.SuggestionContext

internal interface SuspendingInternalSuggestion<S : Any> : InternalSuggestion<S, Choice> {

    suspend fun getSuggestions(
        sender: S,
        current: String,
        arguments: List<String>,
    ): List<Choice>
}

internal class SimpleSuspendingInternalSuggestion<S : Any>(
    private val resolver: SuspendingRichSuggestion<S>,
) : SuspendingInternalSuggestion<S> {

    override suspend fun getSuggestions(
        sender: S,
        current: String,
        arguments: List<String>,
    ): List<Choice> {
        return resolver(SuggestionContext(current, sender, arguments, ""))
    }
}
