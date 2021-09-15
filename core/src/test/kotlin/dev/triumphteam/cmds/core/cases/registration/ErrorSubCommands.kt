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
import dev.triumphteam.cmds.core.annotations.Requirement
import dev.triumphteam.cmds.core.annotations.Requirements
import dev.triumphteam.cmds.core.annotations.SubCommand
import dev.triumphteam.cmds.core.cases.execution.COMMAND_NAME
import dev.triumphteam.cmds.core.cases.execution.SUB_COMMAND_NAME
import dev.triumphteam.cmds.core.flag.Flags
import dev.triumphteam.cmds.core.implementation.TestSender

@Command(COMMAND_NAME)
class EmptyCommandFlags : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class CharacterFlag : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(Flag(flag = "-"))
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class FlagWithCharacters : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(Flag(flag = "flag-name"))
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class SpaceLongFlag : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(Flag(longFlag = "flag with space"))
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class UnregisteredFlagArgument : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @CommandFlags(Flag(flag = "f", argument = Flag::class))
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class FlagArgumentButNoAnnotation : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    fun test(sender: TestSender, flags: Flags) {
    }
}

@Command(COMMAND_NAME)
class InvalidRequirementKey : BaseCommand() {

    @SubCommand(SUB_COMMAND_NAME)
    @Requirements(Requirement("invalid"))
    fun test(sender: TestSender) {
    }

}
