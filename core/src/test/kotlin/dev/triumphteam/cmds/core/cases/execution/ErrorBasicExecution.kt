package dev.triumphteam.cmds.core.cases.execution

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.annotations.Command
import dev.triumphteam.cmds.core.annotations.Default
import dev.triumphteam.cmds.core.implementation.TestSender

const val COMMAND_NAME = "foo"

@Command(COMMAND_NAME)
class DefaultSubCommandNoArgs : BaseCommand() {

    @Default
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class DefaultSubCommandTwoArgs : BaseCommand() {

    @Default
    fun test(sender: TestSender, number: Int, text: String) {
    }
}