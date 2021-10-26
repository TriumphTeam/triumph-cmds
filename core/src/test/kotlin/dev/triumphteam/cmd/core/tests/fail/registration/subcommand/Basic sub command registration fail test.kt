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
package dev.triumphteam.cmd.core.tests.fail.registration.subcommand

import dev.triumphteam.cmd.core.cases.registration.EmptyCommandMethod
import dev.triumphteam.cmd.core.cases.registration.EmptySubCommand
import dev.triumphteam.cmd.core.cases.registration.MissingSender
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmd.core.implementation.TestCommandManager
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