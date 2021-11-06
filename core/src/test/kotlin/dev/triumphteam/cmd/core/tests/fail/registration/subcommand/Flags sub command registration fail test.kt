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

import dev.triumphteam.cmd.core.annotation.Flag
import dev.triumphteam.cmd.core.cases.registration.CharacterFlag
import dev.triumphteam.cmd.core.cases.registration.EmptyCommandFlags
import dev.triumphteam.cmd.core.cases.registration.FlagArgumentButNoAnnotation
import dev.triumphteam.cmd.core.cases.registration.FlagWithCharacters
import dev.triumphteam.cmd.core.cases.registration.InvalidRequirementKey
import dev.triumphteam.cmd.core.cases.registration.SpaceLongFlag
import dev.triumphteam.cmd.core.cases.registration.UnregisteredFlagArgument
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmd.core.implementation.TestCommandManager
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `Flags sub command registration fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `Empty @CommandFlags fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommandFlags())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("@CommandFlags must not be empty")
    }

    @Test
    fun `Character flag fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(CharacterFlag())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Illegal flag name \"-\"")
    }

    @Test
    fun `Flag with illegal characters fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(FlagWithCharacters())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("The flag \"flag-name\" contains an illegal character \"-\"")
    }

    @Test
    fun `Flag with spaces fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(SpaceLongFlag())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("@Flag's identifiers must not contain spaces")
    }

    @Test
    fun `Flag with unregistered argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(UnregisteredFlagArgument())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("@Flag's argument contains unregistered type \"${Flag::class.java.name}\"")
    }

    @Test
    fun `Flag argument but no annotation fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(FlagArgumentButNoAnnotation())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"Flags\" argument found but no \"CommandFlags\" annotation present")
    }

    @Test
    fun `Invalid requirement key fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(InvalidRequirementKey())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Could not find Requirement")
    }

}