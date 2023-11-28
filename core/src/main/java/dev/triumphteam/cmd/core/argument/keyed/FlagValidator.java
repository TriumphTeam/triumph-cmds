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
package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.Nullable;

/**
 * Modified from commons-cli.
 * https://github.com/apache/commons-cli
 */
final class FlagValidator {

    private FlagValidator() {
        throw new AssertionError("Util class must not be initialized.");
    }

    /**
     * Checks whether the flag contains illegal characters.
     *
     * @param flag The {@link String} flag.
     */
    static void validate(final @Nullable String flag) {
        if (flag == null) return;

        // handle the single character flag
        if (flag.length() == 1) {
            char character = flag.charAt(0);

            if (!isValidFlag(character)) {
                throw new CommandRegistrationException("Illegal flag name \"" + character + "\" on flag \"" + flag + "\".");
            }

            return;
        }

        // handle the multi character flag
        for (char character : flag.toCharArray()) {
            if (!isValidChar(character)) {
                throw new CommandRegistrationException("The flag \"" + flag + "\" contains an illegal character \"" + character + "\".");
            }
        }
    }

    /**
     * Returns whether the specified character is a valid Option.
     *
     * @param c the option to validate
     * @return true if <code>c</code> is a letter, <code>?</code> or <code>@</code>, otherwise false.
     */
    private static boolean isValidFlag(char c) {
        return isValidChar(c) || c == '?' || c == '@';
    }

    /**
     * Returns whether the specified character is a valid character.
     *
     * @param c the character to validate
     * @return true if <code>c</code> is a letter.
     */
    private static boolean isValidChar(char c) {
        return Character.isJavaIdentifierPart(c);
    }

}
