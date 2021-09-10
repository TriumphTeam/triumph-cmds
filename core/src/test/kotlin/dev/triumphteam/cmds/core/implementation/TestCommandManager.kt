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
package dev.triumphteam.cmds.core.implementation

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.CommandManager
import dev.triumphteam.cmds.core.command.message.MessageKey
import dev.triumphteam.cmds.core.command.message.context.DefaultMessageContext
import dev.triumphteam.cmds.core.implementation.factory.TestCommandFactory

class TestCommandManager : CommandManager<TestSender>() {
    private val commands = mutableMapOf<String, TestCommand>()

    init {
        registerMessage(MessageKey.UNKNOWN_COMMAND) { sender, _ ->
            sender.result = ExecutionResult.UNKNOWN_COMMAND
        }
        registerMessage(MessageKey.INVALID_ARGUMENT) { sender, _ ->
            sender.result = ExecutionResult.INVALID_ARGUMENT
        }
        registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS) { sender, _ ->
            sender.result = ExecutionResult.NOT_ENOUGH_ARGUMENTS
        }
        registerMessage(MessageKey.TOO_MANY_ARGUMENTS) { sender, _ ->
            sender.result = ExecutionResult.TOO_MANY_ARGUMENTS
        }
    }

    override fun registerCommand(command: BaseCommand) {
        val cliCommand = TestCommandFactory(command, argumentRegistry, requirementRegistry, messageRegistry).create()

        // TODO multiple classes
        if (!cliCommand.addSubCommands(command)) {
            return
        }

        commands[cliCommand.name] = cliCommand
    }

    override fun unregisterCommand(command: BaseCommand) {}

    fun execute(sender: TestSender, commandName: String, args: List<String>) {
        val command = commands[commandName]
        if (command == null) {
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, DefaultMessageContext(args.toList()))
            return
        }

        command.execute(sender, args)
    }

}

fun String.toArgs() = split(" ")