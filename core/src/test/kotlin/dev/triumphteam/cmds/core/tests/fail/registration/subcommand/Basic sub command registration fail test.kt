package dev.triumphteam.cmds.core.tests.fail.registration.subcommand

import dev.triumphteam.cmds.core.cases.registration.EmptyCommandMethod
import dev.triumphteam.cmds.core.cases.registration.EmptySubCommand
import dev.triumphteam.cmds.core.cases.registration.MissingSender
import dev.triumphteam.cmds.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmds.core.implementation.TestCommandManager
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