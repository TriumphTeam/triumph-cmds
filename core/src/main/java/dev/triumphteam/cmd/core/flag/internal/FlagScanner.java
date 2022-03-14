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
package dev.triumphteam.cmd.core.flag.internal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Simple util scanner for easier looping through the tokens.
 */
public final class FlagScanner {

    private final List<String> tokens;
    private int pointer = -1;

    private String current = null;

    public FlagScanner(@NotNull final List<String> tokens) {
        this.tokens = tokens;
    }

    /**
     * Allows peeking into the current token without moving the pointer.
     *
     * @return The current token.
     */
    @NotNull
    public String peek() {
        return current;
    }

    /**
     * Checks if there are more tokens in the list.
     *
     * @return Whether the pointer has reached the list end.
     */
    public boolean hasNext() {
        return pointer < tokens.size() - 1;
    }

    /**
     * Points the pointer to the next token.
     */
    @NotNull
    public String next() {
        if (pointer < tokens.size()) pointer++;
        setToken(tokens.get(pointer));
        return peek();
    }

    /**
     * Points the pointer to the previous token.
     */
    public void previous() {
        if (pointer > 0) pointer--;
        setToken(tokens.get(pointer));
    }

    /**
     * Sets the current token.
     *
     * @param token The new token to set.
     */
    private void setToken(@NotNull final String token) {
        this.current = token;
    }

}
