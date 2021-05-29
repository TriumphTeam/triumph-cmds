package dev.triumphteam.core.tests

import dev.triumphteam.core.exceptions.CommandRegistrationException
import dev.triumphteam.core.implementations.TestCommandManager
import dev.triumphteam.core.tests.command.EmptyCommand
import dev.triumphteam.core.tests.command.NoCommand
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class `Command registration fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `No command registration`() {
        assertThatThrownBy {
            commandManager.registerCommand(NoCommand())
        }.isInstanceOf(CommandRegistrationException::class.java)
    }

    @Test
    fun `Empty command registration`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommand())
        }.isInstanceOf(CommandRegistrationException::class.java)
    }

}