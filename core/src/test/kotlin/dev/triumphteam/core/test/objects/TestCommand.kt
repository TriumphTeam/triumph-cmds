package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.Command
import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.SubCommand
import dev.triumphteam.core.internal.processor.CommonSubCommandProcessor
import java.lang.reflect.Modifier

class TestCommand : Command {

    private val subCommands = mapOf<String, SubCommand>()

    override fun addSubCommands(commandBase: CommandBase) {

        commandBase.javaClass.declaredMethods
            .filter { Modifier.isPublic(it.modifiers) }
            .forEach {
                val subCommandData = CommonSubCommandProcessor.process(it) ?: return@forEach
                println(subCommandData)
            }

    }

}
