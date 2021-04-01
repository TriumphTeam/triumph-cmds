package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.Command
import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.SubCommand
import dev.triumphteam.core.internal.processor.SubCommandProcessor
import java.lang.reflect.Modifier

class TestCommand : Command {

    private val subCommands = mapOf<String, SubCommand>()

    override fun addSubCommands(commandBase: CommandBase): Boolean {

        commandBase.javaClass.declaredMethods
            .filter { Modifier.isPublic(it.modifiers) }
            .forEach {
                val subCommandData = SubCommandProcessor.process(it) ?: return@forEach
                println(subCommandData)
            }

        return subCommands.isNotEmpty()
    }

}
