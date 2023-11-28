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
package dev.triumphteam.cmd.core.annotations;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag annotation. Contains all the "data" for the flag.
 * To be used inside the {@link CommandFlags} annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CommandFlags.class)
public @interface Flag {

    /**
     * Small flag identifier. Isn't required, as long as either flag or long flag has values.
     * Flags must not have spaces.
     *
     * @return The flag's main identifier.
     */
    @NotNull String flag() default "";

    /**
     * Long flag identifier. Isn't required either, as long as either flag or long flag has values.
     * Flags must not have spaces.
     *
     * @return The flag's long identifier.
     */
    @NotNull String longFlag() default "";

    /**
     * Define if the flag should have an argument, and it's type.
     * By default, it uses void, which means no argument is needed.
     *
     * @return The argument type.
     */
    @NotNull Class<?> argument() default void.class;

    /**
     * The value for the {@link SuggestionKey} of a registered suggestion.
     *
     * @return The string key of a suggestion.
     */
    @NotNull String suggestion() default "";

    /**
     * @return The flag's description.
     */
    @NotNull String description() default "";
}
