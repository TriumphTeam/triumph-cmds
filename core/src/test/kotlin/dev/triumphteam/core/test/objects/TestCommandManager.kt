package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.CommandManager
import dev.triumphteam.core.internal.processor.CommonCommandProcessor


class TestCommandManager : CommandManager() {

    override fun registerCommand(command: CommandBase) {
        val data = CommonCommandProcessor.process(command)

        val coreCommand = TestCommand()
        coreCommand.addSubCommands(command)

        register(data.commandName, coreCommand)
        data.aliases.forEach { register(it, coreCommand) }
    }

}