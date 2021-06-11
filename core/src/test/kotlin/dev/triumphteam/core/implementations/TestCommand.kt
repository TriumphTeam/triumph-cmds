package dev.triumphteam.core.implementations

import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.command.Command
import dev.triumphteam.core.command.SubCommand
import java.lang.reflect.Modifier

class TestCommand(private val commandName: String, private val alias: MutableList<String>) : Command {

    private val subCommands = mapOf<String, SubCommand>()

    override fun addSubCommands(baseCommand: BaseCommand): Boolean {

        baseCommand.javaClass.declaredMethods
            .filter { Modifier.isPublic(it.modifiers) }
            .forEach {
                //val subCommandData = TestSubCommandFactory(it).create() ?: return@forEach
                //println(subCommandData)
            }

        return subCommands.isNotEmpty()
    }

    override fun getName(): String {
        return commandName
    }

    override fun getAlias(): MutableList<String> {
        return alias
    }

}
