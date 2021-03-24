package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.Command
import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.SubCommand

class TestCommand : Command {

    private val subCommands = mapOf<String, SubCommand>()

    override fun addSubCommand(commandBase: CommandBase) {

        

    }

}
