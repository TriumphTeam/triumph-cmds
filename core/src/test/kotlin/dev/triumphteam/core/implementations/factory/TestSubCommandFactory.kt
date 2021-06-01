package dev.triumphteam.core.implementations.factory

import dev.triumphteam.core.implementations.TestSubCommand
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory
import java.lang.reflect.Method

class TestSubCommandFactory(method: Method) : AbstractSubCommandFactory<TestSubCommand>(method) {

    override fun create(): TestSubCommand? {
        if (name == null) return null
        return TestSubCommand(name, alias)
    }

}