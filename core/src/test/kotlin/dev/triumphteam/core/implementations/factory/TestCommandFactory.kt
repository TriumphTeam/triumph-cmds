package dev.triumphteam.core.implementations.factory

import dev.triumphteam.core.implementations.TestCommand
import dev.triumphteam.core.internal.BaseCommand
import dev.triumphteam.core.internal.command.factory.AbstractCommandFactory

class TestCommandFactory(baseCommand: BaseCommand) : AbstractCommandFactory<TestCommand>(baseCommand) {

    override fun create(): TestCommand {
        return TestCommand(name, alias)
    }

}