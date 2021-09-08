package dev.triumphteam.core.cases

import dev.triumphteam.core.BaseCommand
import dev.triumphteam.core.annotations.Command

class NoCommand : BaseCommand()

@Command("")
class EmptyCommand : BaseCommand()

class EmptyExtendedCommand : BaseCommand("")