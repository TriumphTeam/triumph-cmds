package dev.triumphteam.core.implementations.factory

import dev.triumphteam.core.implementations.TestSubCommand
import dev.triumphteam.core.internal.command.factory.AbstractSubCommandFactory
import java.lang.reflect.Method

class TestSubCommandFactory(method: Method) : AbstractSubCommandFactory<TestSubCommand>(method) {

    override fun create(): TestSubCommand? {
        if (commandName == null) return null
        return TestSubCommand(commandName, alias)
    }

}