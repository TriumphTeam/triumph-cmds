package dev.triumphteam.core.test

import dev.triumphteam.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.core.test.command.EmptyFunction
import dev.triumphteam.core.test.objects.TestCommandManager
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