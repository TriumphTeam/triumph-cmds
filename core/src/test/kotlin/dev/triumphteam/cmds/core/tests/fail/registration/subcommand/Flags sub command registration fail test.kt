package dev.triumphteam.cmds.core.tests.fail.registration.subcommand

import dev.triumphteam.cmds.core.annotations.Flag
import dev.triumphteam.cmds.core.cases.CharacterFlag
import dev.triumphteam.cmds.core.cases.EmptyCommandFlags
import dev.triumphteam.cmds.core.cases.FlagArgumentButNoAnnotation
import dev.triumphteam.cmds.core.cases.FlagWithCharacters
import dev.triumphteam.cmds.core.cases.SpaceLongFlag
import dev.triumphteam.cmds.core.cases.UnregisteredFlagArgument
import dev.triumphteam.cmds.core.exceptions.SubCommandRegistrationException
import dev.triumphteam.cmds.core.implementation.TestCommandManager
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

}