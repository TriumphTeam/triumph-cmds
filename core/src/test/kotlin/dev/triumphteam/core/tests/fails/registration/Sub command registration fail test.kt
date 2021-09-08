package dev.triumphteam.core.tests.fails.registration

import cmds.implementation.TestCommandManager
import dev.triumphteam.core.cases.EmptyCommandFlags
import dev.triumphteam.core.cases.EmptyCommandMethod
import dev.triumphteam.core.cases.EmptySubCommand
import dev.triumphteam.core.cases.MissingSender
import dev.triumphteam.core.exceptions.SubCommandRegistrationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `Sub command registration fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Empty parameters registration fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommandMethod())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Sub command method's parameters must not be empty")
    }

    @Test
    fun `No sender parameter fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(MissingSender())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Invalid sender parameter")
    }

    @Test
    fun `Empty @SubCommand fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptySubCommand())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"@SubCommand\" name must not be empty")
    }

    @Test
    fun `Empty @CommandFlags fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommandFlags())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"@CommandFlags\" must not be empty")
    }

}