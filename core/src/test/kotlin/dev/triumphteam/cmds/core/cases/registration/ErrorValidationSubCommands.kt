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
package dev.triumphteam.cmds.core.cases.registration

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