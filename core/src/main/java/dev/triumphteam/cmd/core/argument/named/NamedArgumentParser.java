package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public final class NamedArgumentParser {

    private static final char SPACE = ' ';
    private static final char ESCAPE = '\\';
    private static final char SEPARATOR = ':';
    private static final char QUOTE = '"';

    public static Map<String, String> parse(@NotNull final String literal) {
        final PrimitiveIterator.OfInt iterator = literal.chars().iterator();

        final Map<String, String> args = new LinkedHashMap<>();
        final StringBuilder builder = new StringBuilder();

        // Control variables
        boolean escape = false;
        String argument = "";

        while (iterator.hasNext()) {
            final int current = iterator.next();

            // Handles opening and closing of quotes for arg value
            if (current == QUOTE && !argument.isEmpty()) {
                // In case of escaping ignore
                if (escape) {
                    builder.appendCodePoint(QUOTE);
                    escape = false;
                    continue;
                }

                continue;
            }

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
            if (current == SPACE) {
                // If no argument is found, discard values
                if (argument.isEmpty()) {
                    builder.setLength(0);
                    continue;
                }

                // If not in quotes and argument is found, accept as value
                args.put(argument, builder.toString());
                builder.setLength(0);
                argument = "";
                continue;
            }

            // If no escapable token was found, aka ", re-append the backslash
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
