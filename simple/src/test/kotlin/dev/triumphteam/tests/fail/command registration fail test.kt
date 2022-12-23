package dev.triumphteam.tests.fail

import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException
import dev.triumphteam.cmds.simple.SimpleCommandManager
import dev.triumphteam.tests.TestSender
import dev.triumphteam.tests.TestSenderMapper
import dev.triumphteam.tests.TestSenderValidator
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class `command registration fail test` {

    private val commandManager: SimpleCommandManager<TestSender> =
        SimpleCommandManager.create(TestSenderMapper(), TestSenderValidator())

    @Test
    fun `no command registration fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(NoCommand())
        }.isInstanceOf(CommandRegistrationException::class.java)
            .hasMessageContaining("Command name or \"@${Command::class.java.simpleName}\" annotation missing")
    }

    @Test
    fun `empty command registration fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommandAnnotation())
            println("test")
        }.isInstanceOf(CommandRegistrationException::class.java)
            .hasMessageContaining("Command name must not be empty")
    }

    @Test
    fun `empty extend command registration fail`() {
        assertThatThrownBy {
            commandManager.registerCommand(EmptyCommandSuper())
        }.isInstanceOf(CommandRegistrationException::class.java)
            .hasMessageContaining("Command name must not be empty")
    }
}

class NoCommand : BaseCommand()

@Command
class EmptyCommandAnnotation : BaseCommand()

class EmptyCommandSuper : BaseCommand("")
