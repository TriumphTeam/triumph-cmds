package dev.triumphteam.core.tests.fails.registration.subcommand

import dev.triumphteam.core.cases.EmptyCommandMethod
import dev.triumphteam.core.cases.EmptySubCommand
import dev.triumphteam.core.cases.MissingSender
import dev.triumphteam.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.core.implementation.TestCommandManager
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `Basic sub command registration fail test` {

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
            .hasMessageContaining("@SubCommand name must not be empty")
    }

}