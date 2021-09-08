package dev.triumphteam.core.cases

import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.CommandFlags
import dev.triumphteam.core.annotations.Default
import dev.triumphteam.core.annotations.Flag
import dev.triumphteam.core.annotations.SubCommand
import dev.triumphteam.core.command.flag.Flags
import java.io.PrintStream

@Command("foo")
class EmptyCommandFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags
    fun test(sender: PrintStream) {
    }
}

@Command("foo")
class CharacterFlag : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "-"))
    fun test(sender: PrintStream) {
    }
}

@Command("foo")
class FlagWithCharacters : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "flag-name"))
    fun test(sender: PrintStream) {
    }
}

@Command("foo")
class SpaceLongFlag : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(longFlag = "flag with space"))
    fun test(sender: PrintStream) {
    }
}

@Command("foo")
class UnregisteredFlagArgument : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f", argument = Flag::class))
    fun test(sender: PrintStream) {
    }
}

@Command("foo")
class FlagArgumentButNoAnnotation : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, flags: Flags) {
    }
}
