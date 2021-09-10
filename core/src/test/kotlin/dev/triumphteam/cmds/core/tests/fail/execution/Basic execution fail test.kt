package dev.triumphteam.cmds.core.tests.fail.execution

import dev.triumphteam.cmds.core.cases.execution.COMMAND_NAME
import dev.triumphteam.cmds.core.cases.execution.DefaultSubCommandNoArgs
import dev.triumphteam.cmds.core.cases.execution.DefaultSubCommandTwoArgs
import dev.triumphteam.cmds.core.implementation.ExecutionResult
import dev.triumphteam.cmds.core.implementation.TestCommandManager
import dev.triumphteam.cmds.core.implementation.TestSender
import dev.triumphteam.cmds.core.implementation.toArgs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class `Basic execution fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Unknown command fail`() {
        commandManager.registerCommand(DefaultSubCommandNoArgs())
        val sender = TestSender()
        commandManager.execute(sender, "wrong", "".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.UNKNOWN_COMMAND)
    }

    @Test
    fun `Default too many args fail`() {
        commandManager.registerCommand(DefaultSubCommandNoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "this will be too many".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.TOO_MANY_ARGUMENTS)
    }

    @Test
    fun `Default too many args with 2 parameters fail`() {
        commandManager.registerCommand(DefaultSubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "5 will be too many".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.TOO_MANY_ARGUMENTS)
    }

    @Test
    fun `Default not enough args with 2 parameters fail`() {
        commandManager.registerCommand(DefaultSubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "5".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.NOT_ENOUGH_ARGUMENTS)
    }

    @Test
    fun `Default invalid arg fail`() {
        commandManager.registerCommand(DefaultSubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "text text".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.INVALID_ARGUMENT)
    }

}