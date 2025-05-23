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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

import static dev.triumphteam.cmd.core.util.EnumUtils.getEnumConstants;
import static dev.triumphteam.cmd.core.util.EnumUtils.populateCache;

/**
 * An argument type for {@link Enum}s.
 * This is needed instead of the normal {@link ResolverInternalArgument} because of different types of enums, which requires the class.
 *
 * @param <S> The sender type.
 */
public final class EnumInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final Class<? extends Enum<?>> enumType;

    public EnumInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<? extends Enum<?>> type,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, optional);
        this.enumType = type;

        // Populates on creation to reduce runtime of the first run for certain enums, like Bukkit's Material.
        populateCache(type);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param input  The {@link String} argument input.
     * @return An {@link Enum} value of the correct type.
     */
    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input) {
        final String value = input.getInput();
        final WeakReference<? extends Enum<?>> reference = getEnumConstants(enumType).get(value.toUpperCase());

        if (reference == null) {
            return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, value, getName(), getType()));
        }

        final Enum<?> enumValue = reference.get();
        if (enumValue == null) {
            return InternalArgument.invalid((commands, arguments) -> new InvalidArgumentContext(commands, arguments, value, getName(), getType()));
        }

        return InternalArgument.valid(enumValue);
    }

    @Override
    public @NotNull String toString() {
        return "EnumArgument{" +
                "enumType=" + enumType +
                ", super=" + super.toString() + "}";
    }
}
