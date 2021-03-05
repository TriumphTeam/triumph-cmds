package dev.triumphteam.core.test

import dev.triumphteam.core.exceptions.CommandRegistrationException
import dev.triumphteam.core.test.command.EmptyCommand
import dev.triumphteam.core.test.command.NoCommand
import dev.triumphteam.core.test.command.NormalCommand
import dev.triumphteam.core.test.objects.TestCommandManager
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CommandRegistrationTest {

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

    @Test
    fun `Normal command registration`() {
        assertThatCode {
            commandManager.registerCommand(NormalCommand())
        }.doesNotThrowAnyException()
    }

}