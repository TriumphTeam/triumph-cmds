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
import dev.triumphteam.cmds.core.annotations.Default
import dev.triumphteam.cmds.core.command.Command
import dev.triumphteam.cmds.core.command.SimpleSubCommand
import dev.triumphteam.cmds.core.command.SubCommand
import dev.triumphteam.cmds.core.command.argument.ArgumentRegistry
import dev.triumphteam.cmds.core.command.message.MessageKey
import dev.triumphteam.cmds.core.command.message.MessageRegistry
import dev.triumphteam.cmds.core.command.requirement.RequirementRegistry
import dev.triumphteam.cmds.core.implementation.factory.TestSubCommandFactory
import java.lang.reflect.Modifier

class TestCommand(
    private val name: String,
    private val alias: List<String>,
    private val argumentRegistry: ArgumentRegistry<TestSender>,
    private val requirementRegistry: RequirementRegistry<TestSender>,
    private val messageRegistry: MessageRegistry<TestSender>
) : Command {

    private val subCommands: MutableMap<String, SubCommand<TestSender>> = HashMap()
    private val aliases: MutableMap<String, SubCommand<TestSender>> = HashMap()

    override fun addSubCommands(baseCommand: BaseCommand): Boolean {
        var added = false
        val methods = baseCommand.javaClass.declaredMethods

        for (method in methods) {
            if (!Modifier.isPublic(method.modifiers)) continue

            val subCommand: SimpleSubCommand<TestSender> =
                TestSubCommandFactory(baseCommand, method, argumentRegistry, requirementRegistry).create() ?: continue

            added = true

            // TODO add this later and add aliases
            val subCommandName = subCommand.name
            val subCommandAlias = subCommand.alias

            subCommands[subCommandName] = subCommand
            // TODO handle alias later
        }

        return added
    }

    fun execute(sender: TestSender, args: Array<String>) {
        var subCommand = defaultSubCommand
        var subCommandName = ""

        if (args.isNotEmpty()) subCommandName = args[0].lowercase()
        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName)
        }

        if (subCommand == null || args.isNotEmpty() && subCommand.isDefault && subCommand.arguments.isEmpty()) {
            messageRegistry.sendMessage(MessageKey.WRONG_USAGE, sender)
            return
        }

        subCommand.execute(sender, listOf(*args))
        return
    }

    override fun getName(): String {
        return name
    }

    override fun getAlias(): List<String> {
        return alias
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