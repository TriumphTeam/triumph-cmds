package dev.triumphteam.cmds.core.cases

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.annotations.Command
import dev.triumphteam.cmds.core.annotations.CommandFlags
import dev.triumphteam.cmds.core.annotations.Flag
import dev.triumphteam.cmds.core.annotations.Join
import dev.triumphteam.cmds.core.annotations.Optional
import dev.triumphteam.cmds.core.annotations.SubCommand
import dev.triumphteam.cmds.core.command.flag.Flags
import dev.triumphteam.cmds.core.implementation.TestSender

@Command("foo")
class MoreThanOneFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, flags2: Flags) {
    }
}

@Command("foo")
class MoreThanOneLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, @Join text: String, @Join text2: String) {
    }
}

@Command("foo")
class MoreThanOneLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, text: Array<String>, text2: Array<String>) {
    }
}

@Command("foo")
class MoreThanOneLimitlessCollection : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, text: List<String>, text2: List<String>) {
    }
}

@Command("foo")
class WrongOptionalLocation : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, @Optional text: String?, number: Int) {
    }
}

@Command("foo")
class WrongLocationLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, @Join text: String, number: Int) {
    }
}

@Command("foo")
class WrongLocationLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, text: Array<String>, number: Int) {
    }
}

@Command("foo")
class WrongLocationLimitlessCollection : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, text: List<String>, number: Int) {
    }
}

@Command("foo")
class WrongLocationFlags : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, number: Int) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessJoin : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, @Join text: String) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessArray : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, text: Array<String>) {
    }
}

@Command("foo")
class WrongLocationFlagsLimitlessCollection : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, text: List<String>) {
    }
}

@Command("foo")
class UnsupportedCollectionType : BaseCommand() {

    @SubCommand("bar")
    fun test(sender: TestSender, number: Int, text: List<Int>) {
    }
}

@Command("foo")
class UnregisteredType : BaseCommand() {

    @SubCommand("bar")
    @CommandFlags(Flag(flag = "f"))
    fun test(sender: TestSender, flags: Flags, text: Flag) {
    }
}