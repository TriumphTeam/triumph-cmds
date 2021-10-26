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
package dev.triumphteam.cmd.core.cases.execution

import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.CommandFlags
import dev.triumphteam.cmd.core.annotations.Default
import dev.triumphteam.cmd.core.annotations.Flag
import dev.triumphteam.cmd.core.annotations.Requirement
import dev.triumphteam.cmd.core.annotations.Requirements
import dev.triumphteam.cmd.core.flag.Flags
import dev.triumphteam.cmd.core.implementation.TestSender

const val COMMAND_NAME = "foo"

@Command(COMMAND_NAME)
class DefaultSubCommandNoArgs : BaseCommand() {

    @Default
    fun test(sender: TestSender) {
    }
}

@Command(COMMAND_NAME)
class DefaultSubCommandTwoArgs : BaseCommand() {

    @Default
    fun test(sender: TestSender, number: Int, text: String) {
    }
}

@Command(COMMAND_NAME)
class DefaultFlagsWithArguments : BaseCommand() {

    @Default
    @CommandFlags(
        Flag(flag = "a"),
        Flag(
            flag = "n",
            argument = Int::class,
            required = true
        ),
    )
    fun test(sender: TestSender, flags: Flags) {
    }
}

@Command(COMMAND_NAME)
class DefaultDoesNotMeetRequirement : BaseCommand() {

    @Default
    @Requirements(
        Requirement(
            "test.requirement",
            messageKey = "message.key"
        )
    )
    fun test(sender: TestSender, text: String) {
    }
}