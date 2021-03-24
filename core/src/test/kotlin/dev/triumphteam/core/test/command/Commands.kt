package dev.triumphteam.core.test.command

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.annotations.Default
import dev.triumphteam.core.internal.CommandBase

class NoCommand : CommandBase()

@Command()
class EmptyCommand : CommandBase()

@Command("commandName")
class NormalCommand : CommandBase() {

    @Default
    fun defaultFun() {
        
    }

}

class NoAnnotationCommand : CommandBase("command", listOf("alias1"))