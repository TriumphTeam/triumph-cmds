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
package dev.triumphteam.cmd.core.extention.meta;

import dev.triumphteam.cmd.core.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Storage for custom data stored in a {@link Command} or argument.
 * Meta is only mutable during its {@link Builder} state, once it's fully built it'll be fully immutable.
 */
public interface CommandMeta {

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return An {@link Optional} value as {@link V}.
     */
    <V> @NotNull Optional<V> get(final @NotNull MetaKey<V> metaKey);

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return A nullable value as {@link V}.
     */
    <V> @Nullable V getNullable(final @NotNull MetaKey<V> metaKey);

    /**
     * Get the value of the meta associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param def     The default value to return in case the stored value doesn't exist.
     * @param <V>     The type of the value to cast to.
     * @return The value as {@link V} or default {@link V} if it doesn't exist, the return is null if the default value is null.
     */
    @Contract("_, null -> null; _, !null -> !null")
    <V> V getOrDefault(final @NotNull MetaKey<V> metaKey, final @Nullable V def);

    /**
     * Checks if there is any value associated with the passed {@link MetaKey}.
     *
     * @param metaKey The {@link MetaKey} associated with the value.
     * @param <V>     The type of the value to cast to.
     * @return True if the value exists.
     */
    <V> boolean isPresent(final @NotNull MetaKey<V> metaKey);

    /**
     * Get the immutable parent {@link CommandMeta} of this instance.
     *
     * @return The parent meta of this instance.
     */
    @Nullable CommandMeta getParentMeta();

    /**
     * Simple builder to add to or get from the original meta map because it becomes immutable.
     */
    @SuppressWarnings("unchecked")
    final class Builder implements CommandMeta {

        private final Map<MetaKey<?>, Object> metaMap = new HashMap<>();
        private final CommandMeta parentMeta;

        public Builder(final @Nullable CommandMeta parentMeta) {
            this.parentMeta = parentMeta;
        }

        /**
         * Add the value {@link V} to the map associated by the {@link MetaKey}.
         *
         * @param metaKey The {@link MetaKey} to be the key of the internal map.
         * @param value   The nullable value {@link V} to be stored.
         * @param <V>     The type of value that'll be stored.
         */
        public <V> void add(final @NotNull MetaKey<V> metaKey, final @Nullable V value) {
            metaMap.put(metaKey, value);
        }

        /**
         * Add the value {@link V} to the map associated by the {@link MetaKey}.
         * Defaults the value to null.
         *
         * @param metaKey The {@link MetaKey} to be the key of the internal map.
         * @param <V>     The type of value that'll be stored.
         */
        public <V> void add(final @NotNull MetaKey<V> metaKey) {
            add(metaKey, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull <V> Optional<V> get(final @NotNull MetaKey<V> metaKey) {
            return Optional.ofNullable(getNullable(metaKey));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> @Nullable V getNullable(final @NotNull MetaKey<V> metaKey) {
            return (V) metaMap.get(metaKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> @Nullable V getOrDefault(final @NotNull MetaKey<V> metaKey, @Nullable final V def) {
            return (V) metaMap.getOrDefault(metaKey, def);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> boolean isPresent(final @NotNull MetaKey<V> metaKey) {
            return metaMap.containsKey(metaKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @Nullable CommandMeta getParentMeta() {
            return parentMeta;
        }

        /**
         * Creates the final immutable meta.
         * This should not be used externally, but if you need a copy of the current meta state, sure.
         *
         * @return A new {@link CommandMeta} with the current meta map immutable.
         */
        @Contract(" -> new")
        public @NotNull CommandMeta build() {
            return new ImmutableCommandMeta(parentMeta, Collections.unmodifiableMap(metaMap));
        }
    }
}
