package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.CommandManager
import dev.triumphteam.core.internal.command.CommandData


class TestCommandManager : CommandManager() {

    val commands = mutableMapOf<String, TestCommandHandler>()

    override fun registerCommand(command: CommandBase) {
        val commandClass = command.javaClass
        val data = CommandData.from(commandClass)

        println(data.commandName)
        println(data.aliases)
    }

}