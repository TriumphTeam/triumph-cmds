package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.CommandManager
import dev.triumphteam.core.internal.utils.AnnotationUtils


class TestCommandManager : CommandManager() {

    val commands = mutableMapOf<String, TestCommandHandler>()

    override fun registerCommand(command: CommandBase) {
        val commandClass: Class<out CommandBase> = command.javaClass

        val aliases = AnnotationUtils.extractAliases(commandClass)

    }

}