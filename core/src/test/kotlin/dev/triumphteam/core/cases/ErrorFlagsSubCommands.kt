package dev.triumphteam.core.cases

import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.Default
import dev.triumphteam.core.annotations.SubCommand
import dev.triumphteam.core.implementation.TestSender

@Command("foo")
class EmptyCommandMethod : BaseCommand() {

    @Default
    fun test() {
    }
}

@Command("foo")
class MissingSender : BaseCommand() {

    @Default
    fun test(arg: String) {
    }
}

@Command("foo")
class EmptySubCommand : BaseCommand() {

    @SubCommand("")
    fun test(sender: TestSender) {
    }
}
