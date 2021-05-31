package dev.triumphteam.core.tests.fails

import dev.triumphteam.core.implementations.TestCommandManager
import dev.triumphteam.core.tests.command.EmptyFunction
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class `Sub command registration` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Empty function`() {
        assertThatCode {
            commandManager.registerCommand(EmptyFunction())
        }.doesNotThrowAnyException()
    }

}