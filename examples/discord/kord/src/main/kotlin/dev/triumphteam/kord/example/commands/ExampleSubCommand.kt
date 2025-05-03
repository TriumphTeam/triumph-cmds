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
package dev.triumphteam.kord.example.commands

import dev.kord.core.entity.User
import dev.triumphteam.cmd.core.annotations.Command
import dev.triumphteam.cmd.core.annotations.Suggestion
import dev.triumphteam.cmd.core.suggestion.SuggestionContext
import dev.triumphteam.cmds.kord.sender.Sender

@Command("kord-sub")
public class ExampleSubCommand {

    @Suggestion("quick")
    public fun quickSuggestion(context: SuggestionContext<Sender>): List<String> {
        println(context.arguments)
        return listOf(
            "Alexander",
            "Isabella",
            "Benjamin",
            "Charlotte",
            "Daniel",
            "Emma",
            "Gabriel",
            "Hannah",
            "Isaac",
            "Julia"
        )
    }

    @Command("first")
    public suspend fun first(sender: Sender, user: User, @Suggestion("quick") name: String) {
        sender.respondPublic {
            content = "Command sent was /sub first <$name>"
        }
    }

    @Command("second")
    public suspend fun second(sender: Sender, @Suggestion("example") text: String) {
        sender.respondPublic {
            content = "Command sent was /sub second <$text>"
        }
    }
}
