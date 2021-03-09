package dev.triumphteam.cmds

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.SubCommand
import dev.triumphteam.core.internal.CommandBase

fun main() {

}

@Command("command")
class CommandTest : CommandBase() {

    @SubCommand("sub")
    fun sub() {

        @SubCommand("sub sub")
        fun subsub() {
        }

    }

}