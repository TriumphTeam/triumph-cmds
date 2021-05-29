package dev.triumphteam.core.implementations

import dev.triumphteam.core.implementations.factory.TestCommandFactory
import dev.triumphteam.core.internal.BaseCommand
import dev.triumphteam.core.internal.CommandManager


class TestCommandManager : CommandManager<TestCommand>() {

    override fun registerCommand(command: BaseCommand) {

        val testCommand = TestCommandFactory(command).create()

        if (!testCommand.addSubCommands(command)) {
            return
        }

        register(testCommand.commandName, testCommand)
        testCommand.alias.forEach { register(it, testCommand) }
    }

}