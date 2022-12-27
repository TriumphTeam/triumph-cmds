package dev.triumphteam.cmds

import dev.triumphteam.cmd.core.extention.ExtensionBuilder

public fun <S, B : ExtensionBuilder<*, S>> B.useCoroutines() {
    val kotlinArgumentExtension = CoroutinesCommandExtension<S>()
    addCommandMethodProcessor(kotlinArgumentExtension)
    setArgumentValidator(kotlinArgumentExtension)
}
