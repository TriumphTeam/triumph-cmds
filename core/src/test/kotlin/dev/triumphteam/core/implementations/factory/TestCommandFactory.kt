package dev.triumphteam.core.implementations.factory

import dev.triumphteam.core.implementations.TestCommand
import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.command.factory.AbstractCommandFactory

class TestCommandFactory(baseCommand: BaseCommand) : AbstractCommandFactory<TestCommand>(baseCommand) {

    override fun create(): TestCommand {
        return TestCommand(name, alias)
    }

}