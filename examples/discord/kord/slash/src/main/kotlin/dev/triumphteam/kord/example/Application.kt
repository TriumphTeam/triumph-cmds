package dev.triumphteam.kord.example

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.triumphteam.cmds.kord.SlashCommandManager
import dev.triumphteam.kord.example.commands.ExampleCommand
import dev.triumphteam.kord.example.commands.ExampleCommandGroup
import dev.triumphteam.kord.example.commands.ExampleSubCommand

public suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    val manager = SlashCommandManager(kord)

    manager.apply {
        registerCommand(Snowflake(820696172477677628), ExampleCommand())
        registerCommand(Snowflake(820696172477677628), ExampleCommandGroup())
        registerCommand(Snowflake(820696172477677628), ExampleSubCommand())
    }

    kord.login()
}
