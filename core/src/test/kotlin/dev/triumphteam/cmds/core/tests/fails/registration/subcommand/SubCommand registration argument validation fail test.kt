package dev.triumphteam.cmds.core.tests.fails.registration.subcommand

import dev.triumphteam.cmds.core.annotations.Flag
import dev.triumphteam.cmds.core.cases.MoreThanOneFlags
import dev.triumphteam.cmds.core.cases.MoreThanOneLimitlessArray
import dev.triumphteam.cmds.core.cases.MoreThanOneLimitlessCollection
import dev.triumphteam.cmds.core.cases.MoreThanOneLimitlessJoin
import dev.triumphteam.cmds.core.cases.UnregisteredType
import dev.triumphteam.cmds.core.cases.UnsupportedCollectionType
import dev.triumphteam.cmds.core.cases.WrongLocationFlags
import dev.triumphteam.cmds.core.cases.WrongLocationFlagsLimitlessArray
import dev.triumphteam.cmds.core.cases.WrongLocationFlagsLimitlessCollection
import dev.triumphteam.cmds.core.cases.WrongLocationFlagsLimitlessJoin
import dev.triumphteam.cmds.core.cases.WrongLocationLimitlessArray
import dev.triumphteam.cmds.core.cases.WrongLocationLimitlessCollection
import dev.triumphteam.cmds.core.cases.WrongLocationLimitlessJoin
import dev.triumphteam.cmds.core.cases.WrongOptionalLocation
import dev.triumphteam.cmds.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmds.core.implementation.TestCommandManager
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
        assertThatThrownBy {
            commandManager.registerCommand(UnsupportedCollectionType())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("Only String collections are allowed")
    }

    @Test
    fun `Unsupported argument fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(UnregisteredType())
        }.isInstanceOf(SubCommandRegistrationException::class.java)
            .hasMessageContaining("No argument of type \"" + Flag::class.java.name + "\" registered")
    }

}