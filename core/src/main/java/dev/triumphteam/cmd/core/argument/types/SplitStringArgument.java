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
package dev.triumphteam.cmd.core.argument.types;

import dev.triumphteam.cmd.core.argument.Argument;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Splitting argument takes a string and splits it into a collection.
 *
 * @param <S> The sender type.
 */
public final class SplitStringArgument<S> extends StringArgument<S> {

    private final String regex;
    private final Argument<S, String> argument;
    private final Class<?> collectionType;

    public SplitStringArgument(
            @NotNull final String name,
            @NotNull final String regex,
            @NotNull final Argument<S, String> argument,
            @NotNull final Class<?> collectionType,
            final boolean optional
    ) {
        super(name, String.class, optional);
        this.regex = regex;
        this.argument = argument;
        this.collectionType = collectionType;
    }

    /**
     * Takes a string and splits it into a collection.
     *
     * @param sender The sender to resolve to.
     * @param value  The argument value.
     * @return A collection of the split strings.
     */
    @NotNull
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final String value) {
        final Stream<Object> stream = Arrays.stream(value.split(regex)).map(arg -> argument.resolve(sender, arg));
        if (collectionType == Set.class) return stream.collect(Collectors.toSet());
        return stream.collect(Collectors.toList());
    }

}
