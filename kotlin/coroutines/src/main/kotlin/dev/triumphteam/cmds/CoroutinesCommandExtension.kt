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
package dev.triumphteam.cmds

import dev.triumphteam.cmd.core.argument.InternalArgument
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument
import dev.triumphteam.cmd.core.extension.command.CommandExecutor
import dev.triumphteam.cmd.core.extension.ExtensionBuilder
import dev.triumphteam.cmd.core.extension.ValidationResult
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator
import dev.triumphteam.cmd.core.extension.command.Processor
import dev.triumphteam.cmd.core.extension.command.Settings
import dev.triumphteam.cmd.core.extension.meta.CommandMeta
import dev.triumphteam.cmd.core.extension.meta.MetaKey
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

public fun <D, S, B : ExtensionBuilder<D, S>> B.useCoroutines(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineContext),
) {
    val kotlinArgumentExtension = CoroutinesCommandExtension<D, S>(coroutineScope, coroutineContext)
    addProcessor(kotlinArgumentExtension)
    setArgumentValidator(kotlinArgumentExtension)
    setCommandExecutor(kotlinArgumentExtension)
}

public class CoroutinesCommandExtension<D, S>(
    private val coroutineScope: CoroutineScope,
    private val coroutineContext: CoroutineContext,
) : Processor<D, S>,
    ArgumentValidator<S, ST>,
    CommandExecutor<S> {

    private companion object {
        /** The key that'll represent a suspending function. */
        private val SUSPEND_META_KEY: MetaKey<Unit> = MetaKey.of("suspend", Unit::class.java)
    }

    /** Simply processing if the [element] contains a [Continuation], if so, we're dealing with a suspend function. */
    override fun process(
        element: AnnotatedElement,
        target: ProcessorTarget,
        meta: CommandMeta.Builder,
        settingsBuilder: Settings.Builder<D, S>,
    ) {
        if (element !is Method) return
        // Not really necessary but doesn't hurt to check
        if (target != ProcessorTarget.COMMAND) return

        if (element.parameterTypes.none(Continuation::class.java::equals)) return
        // Marks the function as suspending
        // We don't really care about the value it passes
        meta.add(SUSPEND_META_KEY, Unit)
    }

    /** Validation uses the same as the defaults but with an addition modification to allow [Continuation]. */
    override fun validate(
        meta: CommandMeta,
        argument: InternalArgument<S, ST>,
        position: Int,
        last: Int,
    ): ValidationResult<String> {
        // If we're dealing with a suspending function, the meta will be present
        val suspend = meta.parentMeta?.isPresent(SUSPEND_META_KEY) ?: false

        // If we're dealing with a suspending function, the visible "last" argument is one position less, because
        // the last one is always a Continuation
        val suspendLast = if (suspend) last - 1 else last

        // Validation for optionals
        if (position != suspendLast && argument.isOptional) {
            return invalid("Optional internalArgument is only allowed as the last internalArgument")
        }

        // Validation for limitless
        if (position != suspendLast && argument is LimitlessInternalArgument<*>) {
            return invalid("Limitless internalArgument is only allowed as the last internalArgument")
        }

        // A continuation found
        if (argument.type == Continuation::class.java) {
            // Continuation is not allowed as an argument
            if (position != last) {
                return invalid("Kotlin continuation is not allowed as an argument, make the function suspend instead")
            }

            // We ignore this type for execution later
            return ignore()
        }

        // Could not find the type registered
        if (argument is UnknownInternalArgument<*>) {
            return invalid("No internalArgument of type \"" + argument.getType().name + "\" registered")
        }

        // If everything goes well, we now have valid argument
        return valid()
    }

    /** Executes the command with normal reflection or call suspending if the method is suspending. */
    override fun execute(
        meta: CommandMeta,
        messageRegistry: MessageRegistry<S?>,
        sender: S & Any,
        instance: Any,
        method: Method,
        arguments: List<Any?>,
    ) {
        if (meta.isPresent(SUSPEND_META_KEY)) {
            coroutineScope.launch(coroutineContext) {
                handleResult(
                    meta,
                    messageRegistry,
                    sender,
                    method.kotlinFunction?.callSuspend(instance, *arguments.toTypedArray())
                )
            }
            return
        }

        handleResult(meta, messageRegistry, sender, method.invoke(instance, *arguments.toTypedArray()))
    }
}
