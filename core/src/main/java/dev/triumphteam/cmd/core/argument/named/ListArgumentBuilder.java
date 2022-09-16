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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ListArgumentBuilder extends AbstractArgumentBuilder<ListArgumentBuilder> {

    private final Class<?> collectionType;
    private String separator = ",";

    public ListArgumentBuilder(final @NotNull Class<?> collectionType, final @NotNull Class<?> type) {
        super(type);
        this.collectionType = collectionType;
    }

    @Contract("_ -> this")
    public @NotNull ListArgumentBuilder separator(final @NotNull String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Builds the argument.
     *
     * @return A new {@link Argument} with the data from this builder.
     */
    @Contract(" -> new")
    public @NotNull Argument build() {
        return new ListArgument(this);
    }

    @NotNull Class<?> getCollectionType() {
        return collectionType;
    }

    @NotNull String getSeparator() {
        return separator;
    }
}
