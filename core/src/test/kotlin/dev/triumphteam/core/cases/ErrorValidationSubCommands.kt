package dev.triumphteam.core.cases

import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.CommandFlags
import dev.triumphteam.core.annotations.Flag
import dev.triumphteam.core.annotations.Join
import dev.triumphteam.core.annotations.Optional
import dev.triumphteam.core.annotations.SubCommand
import dev.triumphteam.core.command.flag.Flags
import java.io.PrintStream

@Command("foo")
class MoreThanOneFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: PrintStream, flags: Flags, flags2: Flags) {
    }
}

@Command("foo")
class MoreThanOneLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, @Join text: String, @Join text2: String) {
    }
}

@Command("foo")
class MoreThanOneLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, text: Array<String>, text2: Array<String>) {
    }
}

@Command("foo")
class MoreThanOneLimitlessList : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, text: List<String>, text2: List<String>) {
    }
}

@Command("foo")
class WrongOptionalLocation : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, @Optional text: String?, number: Int) {
    }
}

@Command("foo")
class WrongLocationLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, @Join text: String, number: Int) {
    }
}

// TODO
@Command("foo")
class WrongLocationLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, text: Array<String>, number: Int) {
    }
}

@Command("foo")
class WrongLocationLimitlessList : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: PrintStream, text: List<String>, number: Int) {
    }
}

@Command("foo")
class WrongLocationFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: PrintStream, flags: Flags, number: Int) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: PrintStream, flags: Flags, @Join text: String) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: PrintStream, flags: Flags, text: Array<String>) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessList : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: PrintStream, flags: Flags, text: List<String>) {
    }
}