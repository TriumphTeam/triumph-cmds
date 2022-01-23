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

import dev.triumphteam.cmd.core.Command
import dev.triumphteam.cmd.core.annotation.Flag
import dev.triumphteam.cmd.core.cases.registration.MoreThanOneFlags
import dev.triumphteam.cmd.core.cases.registration.MoreThanOneLimitlessArray
import dev.triumphteam.cmd.core.cases.registration.MoreThanOneLimitlessCollection
import dev.triumphteam.cmd.core.cases.registration.MoreThanOneLimitlessJoin
import dev.triumphteam.cmd.core.cases.registration.UnregisteredType
import dev.triumphteam.cmd.core.cases.registration.UnsupportedCollectionType
import dev.triumphteam.cmd.core.cases.registration.WrongLocationFlags
import dev.triumphteam.cmd.core.cases.registration.WrongLocationFlagsLimitlessArray
import dev.triumphteam.cmd.core.cases.registration.WrongLocationFlagsLimitlessCollection
import dev.triumphteam.cmd.core.cases.registration.WrongLocationFlagsLimitlessJoin
import dev.triumphteam.cmd.core.cases.registration.WrongLocationLimitlessArray
import dev.triumphteam.cmd.core.cases.registration.WrongLocationLimitlessCollection
import dev.triumphteam.cmd.core.cases.registration.WrongLocationLimitlessJoin
import dev.triumphteam.cmd.core.cases.registration.WrongOptionalLocation
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmd.core.implementation.TestCommandManager
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `SubCommand registration argument validation fail test` {

    private val commandManager = TestCommandManager()

    @Test
    fun `More than one flags argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(MoreThanOneFlags())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("More than one \"Flags\" argument declared")
    }

    @Test
    fun `More than one limitless join argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(MoreThanOneLimitlessJoin())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("More than one limitless argument declared")
    }

    @Test
    fun `More than one limitless collection argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(MoreThanOneLimitlessCollection())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("More than one limitless argument declared")
    }

    @Test
    fun `More than one limitless array argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(MoreThanOneLimitlessArray())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("More than one limitless argument declared")
    }

    @Test
    fun `Optional wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongOptionalLocation())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Optional argument is only allowed as the last argument")
    }

    @Test
    fun `Limitless join argument wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationLimitlessJoin())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Limitless argument must be the last argument if \"Flags\" is not present")
    }

    @Test
    fun `Limitless collection argument wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationLimitlessCollection())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Limitless argument must be the last argument if \"Flags\" is not present")
    }

    @Test
    fun `Limitless array argument wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationLimitlessArray())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Limitless argument must be the last argument if \"Flags\" is not present")
    }

    @Test
    fun `Flags argument wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationFlags())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"Flags\" argument must always be the last argument")
    }

    @Test
    fun `Flags and limitless join arguments wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationFlagsLimitlessJoin())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"Flags\" argument must always be after a limitless argument")
    }

    @Test
    fun `Flags and limitless collection arguments wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationFlagsLimitlessCollection())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"Flags\" argument must always be after a limitless argument")
    }

    @Test
    fun `Flags and limitless array arguments wrong location fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(WrongLocationFlagsLimitlessArray())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("\"Flags\" argument must always be after a limitless argument")
    }

    @Test
    fun `Unsupported collection argument type fail`() {
        UnsupportedCollectionType()
        assertThatThrownBy {
            commandManager.registerCommand(UnsupportedCollectionType())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("No argument of type \"${Command::class.java.name}\" registered.")
    }

    @Test
    fun `Unsupported argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(UnregisteredType())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("No argument of type \"${Flag::class.java.name}\" registered")
    }

}