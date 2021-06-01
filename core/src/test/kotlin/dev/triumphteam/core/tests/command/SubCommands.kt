package dev.triumphteam.core.tests.command

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.Default
import dev.triumphteam.core.annotations.SubCommand
import dev.triumphteam.core.BaseCommand

@Command("foo")
class EmptyFunction : BaseCommand() {

    @Default(alias = ["alias"])
    fun test() {
    }

    @SubCommand("", alias = ["alias"])
    fun test2() {

    }

}