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
package dev.triumphteam.cmd.core.implementation

import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.Command
import dev.triumphteam.cmd.core.SubCommand
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.implementation.factory.TestCommandProcessor
import java.lang.reflect.Modifier

class TestCommand(processor: TestCommandProcessor) : Command {

    //private val name = processor.name
    //private val argumentRegistry = processor.argumentRegistry
    //private val requirementRegistry = processor.requirementRegistry
    //private val messageRegistry = processor.messageRegistry

    private val subCommands: MutableMap<String, SubCommand<TestSender>> = HashMap()
    private val aliases: MutableMap<String, SubCommand<TestSender>> = HashMap()

    override fun addSubCommands(baseCommand: BaseCommand) {

        for (method in baseCommand.javaClass.declaredMethods) {
            if (!Modifier.isPublic(method.modifiers)) continue

            //val processor = TestSubCommandProcessor(baseCommand, method, argumentRegistry, requirementRegistry, messageRegistry)

          //  val subCommandName = processor.name ?: continue
            //val subCommand = AbstractSubCommand(processor, name)

            //subCommands[subCommandName] = subCommand
            // TODO handle alias later

        }

    }

    fun execute(sender: TestSender, args: List<String>) {
        var subCommand = defaultSubCommand

        var subCommandName = ""
        if (args.isNotEmpty()) subCommandName = args[0].lowercase()

        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName)
        }

        if (subCommand == null) {
           /* messageRegistry.sendMessage(
                MessageKey.UNKNOWN_COMMAND, sender,
                DefaultMessageContext(
                    name,
                    ""
                )
            );*/
            return
        }

        subCommand.execute(sender, args)
        return
    }

    private val defaultSubCommand: SubCommand<TestSender>?
        get() = subCommands[Default.DEFAULT_CMD_NAME]

    private fun getSubCommand(key: String): SubCommand<TestSender>? {
        val subCommands = subCommands[key]
        return subCommands ?: aliases[key]
    }

    private fun subCommandExists(key: String): Boolean {
        return subCommands.containsKey(key) || aliases.containsKey(key)
    }
}