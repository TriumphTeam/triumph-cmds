package dev.triumphteam.cmds.core.tests.fail.execution

import dev.triumphteam.cmds.core.cases.execution.DefaultSubCommandNoArgs
import dev.triumphteam.cmds.core.implementation.ExecutionResult
import dev.triumphteam.cmds.core.implementation.TestCommandManager
import dev.triumphteam.cmds.core.implementation.TestSender
import dev.triumphteam.cmds.core.implementation.toArgs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class `Command fails test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Unknown command fail`() {
        commandManager.registerCommand(DefaultSubCommandNoArgs())
        val sender = TestSender()
        commandManager.execute(sender, "wrong", "".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.UNKNOWN_COMMAND)
    }

}