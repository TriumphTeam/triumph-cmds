package dev.triumphteam.core.tests.command

import dev.triumphteam.core.annotations.Command
import dev.triumphteam.core.BaseCommand

class NoCommand : BaseCommand()

@Command("")
class EmptyCommand : BaseCommand()

@Command("commandName")
class NormalCommand : BaseCommand()

@Command("commandName", alias = ["alias", "alias2"])
class AnnotationAlias : BaseCommand()

class NoAnnotationCommand : BaseCommand("commandName", listOf("alias", "alias2"))