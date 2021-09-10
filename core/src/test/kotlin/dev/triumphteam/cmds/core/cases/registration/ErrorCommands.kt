package dev.triumphteam.cmds.core.cases.registration

import dev.triumphteam.cmds.core.BaseCommand
import dev.triumphteam.cmds.core.annotations.Command

class NoCommand : BaseCommand()

@Command("")
class EmptyCommand : BaseCommand()

class EmptyExtendedCommand : BaseCommand("")