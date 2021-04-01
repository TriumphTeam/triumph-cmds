package dev.triumphteam.core.test.objects

import dev.triumphteam.core.internal.CommandBase
import dev.triumphteam.core.internal.CommandManager
import dev.triumphteam.core.internal.processor.CommandProcessor


class TestCommandManager : CommandManager() {

    override fun registerCommand(command: CommandBase) {
        val data = CommandProcessor.process(command)

        val coreCommand = TestCommand()

        if (!coreCommand.addSubCommands(command)) {
            return
        }

        register(data.commandName, coreCommand)
        data.aliases.forEach { register(it, coreCommand) }
    }

}