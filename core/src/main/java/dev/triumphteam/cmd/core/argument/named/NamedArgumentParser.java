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
package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public final class NamedArgumentParser {

    private static final char SPACE = ' ';
    private static final char ESCAPE = '\\';
    private static final char SEPARATOR = ':';

    public static Map<String, String> parse(final @NotNull String literal) {
        final PrimitiveIterator.OfInt iterator = literal.chars().iterator();

        final Map<String, String> args = new LinkedHashMap<>();
        final StringBuilder builder = new StringBuilder();

        // Control variables
        boolean escape = false;
        String argument = "";

        while (iterator.hasNext()) {
            final int current = iterator.next();

            // Marks next character to be escaped
            if (current == ESCAPE && !argument.isEmpty()) {
                escape = true;
                continue;
            }

            // Found a separator
            if (current == SEPARATOR && argument.isEmpty()) {
                argument = builder.toString();
                builder.setLength(0);
                continue;
            }

            // Handling for spaces
            // TODO: Scape
            if (current == SPACE) {
                // If no argument is found, discard values
                if (argument.isEmpty()) {
                    builder.setLength(0);
                    continue;
                }

                // If argument is found, accept as value
                args.put(argument, builder.toString());
                builder.setLength(0);
                argument = "";
                continue;
            }

            // If no escapable token was found, aka :, re-append the backslash
            if (escape) {
                builder.appendCodePoint(ESCAPE);
                escape = false;
            }

            // Normal append character
            builder.appendCodePoint(current);
        }

        // If end of string is reached and value was not closed, close it
        if (!argument.isEmpty()) args.put(argument, builder.toString());

        return args;
    }
}
