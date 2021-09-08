package dev.triumphteam.cmds.core.cases

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.annotations.Command
import dev.triumphteam.cmds.core.annotations.Default
import dev.triumphteam.cmds.core.annotations.SubCommand
import dev.triumphteam.cmds.core.implementation.TestSender

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
