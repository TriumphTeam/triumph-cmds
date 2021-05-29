package dev.triumphteam.core.tests.succeeds

import dev.triumphteam.core.implementations.TestCommandManager
import dev.triumphteam.core.tests.command.AnnotationAlias
import dev.triumphteam.core.tests.command.NoAnnotationCommand
import dev.triumphteam.core.tests.command.NormalCommand
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class `Command registration success test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Normal command registration`() {
        assertThatCode {
            commandManager.registerCommand(NormalCommand())
        }.doesNotThrowAnyException()
    }

    @Test
    fun `Normal command registration with alias`() {
        assertThatCode {
            commandManager.registerCommand(AnnotationAlias())
        }.doesNotThrowAnyException()
    }

    @Test
    fun `No annotation command registration`() {
        assertThatCode {
            commandManager.registerCommand(NoAnnotationCommand())
        }.doesNotThrowAnyException()
    }

}