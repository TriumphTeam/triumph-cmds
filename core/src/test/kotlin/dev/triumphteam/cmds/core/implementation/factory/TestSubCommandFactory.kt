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
package dev.triumphteam.cmds.core.implementation.factory

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.SimpleSubCommand
import dev.triumphteam.cmds.core.argument.ArgumentRegistry
import dev.triumphteam.cmds.core.factory.AbstractSubCommandFactory
import dev.triumphteam.cmds.core.implementation.TestSender
import dev.triumphteam.cmds.core.message.MessageRegistry
import dev.triumphteam.cmds.core.requirement.RequirementRegistry
import java.lang.reflect.Method

class TestSubCommandFactory(
    baseCommand: BaseCommand,
    method: Method,
    argumentRegistry: ArgumentRegistry<TestSender>,
    requirementRegistry: RequirementRegistry<TestSender>,
    messageRegistry: MessageRegistry<TestSender>
) : AbstractSubCommandFactory<TestSender, SimpleSubCommand<TestSender>>(
    baseCommand,
    method,
    argumentRegistry,
    requirementRegistry,
    messageRegistry
) {

    private var senderClass: Class<*>? = null

    override fun create(parentName: String): SimpleSubCommand<TestSender>? {
        if (name == null) return null
        return SimpleSubCommand(this, parentName)
    }

    override fun extractArguments(method: Method) {
        val parameters = method.parameters

        if (parameters.isEmpty()) {
            throw createException("Sub command method's parameters must not be empty")
        }

        for (i in parameters.indices) {
            val parameter = parameters[i]
            if (i == 0) {
                if (!TestSender::class.java.isAssignableFrom(parameter.type)) {
                    throw createException("Invalid sender parameter \"${parameter.type.name}\"")
                }
                senderClass = parameter.type
                continue
            }
            createArgument(parameter)
        }
    }
}