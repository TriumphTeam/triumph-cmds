package dev.triumphteam.core.tests.command

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.internal.BaseCommand

@Command("foo")
class EmptyFunction : BaseCommand() {

    fun emptyFunction() {}

}