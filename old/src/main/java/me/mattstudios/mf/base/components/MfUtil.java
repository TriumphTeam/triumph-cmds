/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
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

package me.mattstudios.mf.base.components;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public final class MfUtil {

    /**
     * Utility to use color codes easily
     *
     * @param message The message String
     * @return returns the string with color
     */
    public static String color(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Utility to use color codes easily
     *
     * @param messages The messages list
     * @return returns the string list with color
     */
    public static List<String> color(final List<String> messages) {
        return messages.parallelStream().map(message -> ChatColor.translateAlternateColorCodes('&', message)).collect(Collectors.toList());
    }

}
