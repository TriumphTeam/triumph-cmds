package dev.triumphteam.cmds.core.cases

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.annotations.Command
import dev.triumphteam.cmds.core.annotations.CommandFlags
import dev.triumphteam.cmds.core.annotations.Flag
import dev.triumphteam.cmds.core.annotations.SubCommand
import dev.triumphteam.cmds.core.command.flag.Flags
import dev.triumphteam.cmds.core.implementation.TestSender

@Command("foo")
class EmptyCommandFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags
    fun test(sender: TestSender) {
    }
}

@Command("foo")
class CharacterFlag : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "-"))
    fun test(sender: TestSender) {
    }
}

@Command("foo")
class FlagWithCharacters : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "flag-name"))
    fun test(sender: TestSender) {
    }
}

@Command("foo")
class SpaceLongFlag : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(longFlag = "flag with space"))
    fun test(sender: TestSender) {
    }
}

@Command("foo")
class UnregisteredFlagArgument : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f", argument = Flag::class))
    fun test(sender: TestSender) {
    }
}

@Command("foo")
class FlagArgumentButNoAnnotation : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, flags: Flags) {
    }
}
