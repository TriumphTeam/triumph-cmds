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
package dev.triumphteam.cmds.kotlin

import dev.triumphteam.cmd.core.flag.Flags

/**
 * Checks if the flag key is present or not using operator function.
 * Useful for simple flags like `-l`.
 * Where you just want to check if the flag was added or not.
 * For flag with values recommended [getValueOrNull].
 */
public operator fun Flags.contains(flag: String): Boolean = this.hasFlag(flag)

/**
 * Gets the flag value in a not nullable way, using reified types.
 * However, it'll throw exception if the flag isn't present.
 * Recommended use for required flags with required argument.
 */
public inline fun <reified T> Flags.getValue(flag: String): T {
    return this.getValue(flag, T::class.java)
}

/**
 * Gets the flag value in a nullable way, using reified types.
 */
public inline fun <reified T> Flags.getValueOrNull(flag: String): T? = this.getValueOrNull(flag, T::class.java)

/**
 * Gets the flag value in a not nullable way since a default value will be given, using reified types.
 */
public inline fun <reified T : Any> Flags.getValueOrDefault(flag: String, default: T): T =
    this.getValueOrDefault(flag, T::class.java, default)