package dev.triumphteam.cmds

import dev.triumphteam.cmd.core.argument.InternalArgument
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidationResult
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator
import dev.triumphteam.cmd.core.extention.argument.CommandMetaProcessor
import dev.triumphteam.cmd.core.extention.meta.CommandMeta
import dev.triumphteam.cmd.core.extention.meta.MetaKey
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.coroutines.Continuation

public class CoroutinesCommandExtension<S> : CommandMetaProcessor, ArgumentValidator<S> {

    private companion object {
        /** The key that'll represent a suspending function. */
        private val SUSPEND_META_KEY: MetaKey<Unit> = MetaKey.of("suspend", Unit::class.java)
    }

    /** Simply processing if the [element] contains a [Continuation], if so, we're dealing with a suspend function. */
    override fun process(element: AnnotatedElement, target: ProcessorTarget, meta: CommandMeta.Builder) {
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
        argument: InternalArgument<S, *>,
        position: Int,
        last: Int,
    ): ArgumentValidationResult {
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
}
