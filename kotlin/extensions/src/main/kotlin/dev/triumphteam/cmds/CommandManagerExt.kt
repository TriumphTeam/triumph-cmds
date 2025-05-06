package dev.triumphteam.cmds

import dev.triumphteam.cmd.core.CommandManager
import dev.triumphteam.cmd.core.argument.ArgumentResolver

public inline fun <reified T, D, S, ST> CommandManager<*, *, D, S, ST>.registerArgument(resolver: ArgumentResolver<S>) {
    registerArgument(T::class.java, resolver)
}
