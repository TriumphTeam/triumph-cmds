package dev.triumphteam.core.test.command

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.internal.CommandBase

@Command("foo")
class EmptyFunction : CommandBase() {

    fun emptyFunction() {}

}