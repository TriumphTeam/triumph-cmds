package dev.triumphteam.core.tests.fails

import dev.triumphteam.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.core.implementations.TestCommandManager
import dev.triumphteam.core.tests.command.EmptyFunction
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class `Sub command registration` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Empty function`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyFunction())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("")
    }

}