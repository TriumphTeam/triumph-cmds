package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Modified from commons-cli.
 * https://github.com/apache/commons-cli
 */
public final class FlagValidator {

    /**
     * Checks whether or not the flag contains illegal characters.
     *
     * @param flag   The {@link String} flag.
     * @param method The method from the registration so that better error message can be thrown.
     */
    public static void validate(@Nullable final String flag, @NotNull final Method method) {
        if (flag == null) return;

        // handle the single character flag
        if (flag.length() == 1) {
            char character = flag.charAt(0);

            if (!isValidOpt(character)) {
                throw new SubCommandRegistrationException("Illegal flag name (" + character + ").", method);
            }

            return;
        }

        // handle the multi character opt
        for (char character : flag.toCharArray()) {
            if (!isValidChar(character)) {
                throw new SubCommandRegistrationException(
                        "The flag (" + flag + ") contains an illegal character (" + character + ").",
                        method
                );
            }
        }
    }

    /**
     * Returns whether the specified character is a valid Option.
     *
     * @param c the option to validate
     * @return true if <code>c</code> is a letter, '?' or '@', otherwise false.
     */
    private static boolean isValidOpt(char c) {
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
