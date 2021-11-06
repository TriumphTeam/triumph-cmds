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
package dev.triumphteam.cmd.core.cases.registration

import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.CommandFlags
import dev.triumphteam.cmd.core.annotation.Flag
import dev.triumphteam.cmd.core.annotation.Join
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.core.cases.execution.COMMAND_NAME
import dev.triumphteam.cmd.core.cases.execution.SUB_COMMAND_NAME
import dev.triumphteam.cmd.core.flag.Flags
import dev.triumphteam.cmd.core.implementation.TestSender

@Command(COMMAND_NAME)
class MoreThanOneFlags : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, flags2: Flags) {
    }
}

@Command(COMMAND_NAME)
class MoreThanOneLimitlessJoin : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, @Join text: String, @Join text2: String) {
    }
}

@Command(COMMAND_NAME)
class MoreThanOneLimitlessArray : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, text: Array<String>, text2: Array<String>) {
    }
}

@Command(COMMAND_NAME)
class MoreThanOneLimitlessCollection : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, text: List<String>, text2: List<String>) {
    }
}

@Command(COMMAND_NAME)
class WrongOptionalLocation : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, @Optional text: String?, number: Int) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationLimitlessJoin : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, @Join text: String, number: Int) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationLimitlessArray : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, text: Array<String>, number: Int) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationLimitlessCollection : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, text: List<String>, number: Int) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationFlags : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, number: Int) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationFlagsLimitlessJoin : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, @Join text: String) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationFlagsLimitlessArray : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, text: Array<String>) {
    }
}

@Command(COMMAND_NAME)
class WrongLocationFlagsLimitlessCollection : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, text: List<String>) {
    }
}

@Command(COMMAND_NAME)
class UnsupportedCollectionType : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, number: Int, text: List<Int>) {
    }
}

@Command(COMMAND_NAME)
class UnregisteredType : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(
        Flag(
            flag = "f"
        )
    )
    fun test(sender: TestSender, flags: Flags, text: Flag) {
    }
}