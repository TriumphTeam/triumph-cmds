/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmds.core.tests.fail.execution

import dev.triumphteam.cmds.core.cases.execution.COMMAND_NAME
import dev.triumphteam.cmds.core.cases.execution.SUB_COMMAND_NAME
import dev.triumphteam.cmds.core.cases.execution.SubCommandDoesNotMeetRequirement
import dev.triumphteam.cmds.core.cases.execution.SubCommandFlagsWithArguments
import dev.triumphteam.cmds.core.cases.execution.SubCommandNoArgs
import dev.triumphteam.cmds.core.cases.execution.SubCommandTwoArgs
import dev.triumphteam.cmds.core.implementation.ExecutionResult
import dev.triumphteam.cmds.core.implementation.TestCommandManager
import dev.triumphteam.cmds.core.implementation.TestSender
import dev.triumphteam.cmds.core.implementation.toArgs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class `SubCommand execution fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Sub command too many args fail`() {
        commandManager.registerCommand(SubCommandNoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME this will be too many".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.TOO_MANY_ARGUMENTS)
    }

    @Test
    fun `Sub command too many args with 2 parameters fail`() {
        commandManager.registerCommand(SubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME 5 will be too many".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.TOO_MANY_ARGUMENTS)
    }

    @Test
    fun `Sub command not enough args with 2 parameters fail`() {
        commandManager.registerCommand(SubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME 5".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.NOT_ENOUGH_ARGUMENTS)
    }

    @Test
    fun `Sub command invalid arg fail`() {
        commandManager.registerCommand(SubCommandTwoArgs())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME text text".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.INVALID_ARGUMENT)
    }

    @Test
    fun `Sub command missing flag fail`() {
        commandManager.registerCommand(SubCommandFlagsWithArguments())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME text".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.MISSING_REQUIRED_FLAG)
    }

    @Test
    fun `Sub command missing flag argument fail`() {
        commandManager.registerCommand(SubCommandFlagsWithArguments())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME text -n".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.MISSING_REQUIRED_FLAG_ARGUMENT)
    }

    @Test
    fun `Sub command invalid flag argument fail`() {
        commandManager.registerCommand(SubCommandFlagsWithArguments())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME text -n not-number".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.INVALID_FLAG_ARGUMENT)
    }

    @Test
    fun `Sub command does not meet requirements fail`() {
        commandManager.registerCommand(SubCommandDoesNotMeetRequirement())
        val sender = TestSender()
        commandManager.execute(sender, COMMAND_NAME, "$SUB_COMMAND_NAME text".toArgs())

        Assertions.assertThat(sender.result).isEqualTo(ExecutionResult.DOES_NOT_MEET_REQUIREMENTS)
    }

}