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
    manager.registerSuggestion(String::class.java) { _, _ -> listOf("ass", "ss") }

    manager.apply {
        registerCommand(Snowflake(820696172477677628), ExampleCommand())
        registerCommand(Snowflake(820696172477677628), ExampleCommandGroup())
        registerCommand(Snowflake(820696172477677628), ExampleSubCommand())
    }

    kord.login()
}
