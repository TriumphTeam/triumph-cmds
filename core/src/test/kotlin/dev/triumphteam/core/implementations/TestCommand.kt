package dev.triumphteam.core.implementations

import dev.triumphteam.core.implementations.factory.TestSubCommandFactory
import dev.triumphteam.core.internal.BaseCommand
import dev.triumphteam.core.internal.SubCommand
import dev.triumphteam.core.internal.command.Command
import java.lang.reflect.Modifier

class TestCommand(private val commandName: String, private val alias: MutableList<String>) : Command {

    private val subCommands = mapOf<String, SubCommand>()

    override fun addSubCommands(baseCommand: BaseCommand): Boolean {

        baseCommand.javaClass.declaredMethods
            .filter { Modifier.isPublic(it.modifiers) }
            .forEach {
                val subCommandData = TestSubCommandFactory(it).create() ?: return@forEach
                println(subCommandData)
            }

        return subCommands.isNotEmpty()
    }

    override fun isAlias(): Boolean {
        return false
    }

    override fun getName(): String {
        return commandName
    }

    override fun getAlias(): MutableList<String> {
        return alias
    }

}
