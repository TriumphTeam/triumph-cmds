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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
final class ImmutableCommandMeta implements CommandMeta {

    private final CommandMeta parentMeta;
    private final Map<MetaKey<?>, Object> meta;

    public ImmutableCommandMeta(
            final @Nullable CommandMeta parentMeta,
            final @NotNull Map<MetaKey<?>, Object> meta
    ) {
        this.parentMeta = parentMeta;
        this.meta = meta;
    }

    @Override
    public @NotNull <V> Optional<V> get(final @NotNull MetaKey<V> metaKey) {
        return Optional.ofNullable(getNullable(metaKey));
    }

    @Override
    public <V> @Nullable V getNullable(final @NotNull MetaKey<V> metaKey) {
        return (V) meta.get(metaKey);
    }

    @Override
    public <V> V getOrDefault(final @NotNull MetaKey<V> metaKey, @Nullable final V def) {
        return (V) meta.getOrDefault(metaKey, def);
    }

    @Override
    public <V> boolean isPresent(final @NotNull MetaKey<V> metaKey) {
        return meta.containsKey(metaKey);
    }

    @Override
    public @Nullable CommandMeta getParentMeta() {
        return parentMeta;
    }

    @Override
    public String toString() {
        return "ImmutableCommandMeta{" +
                "meta=" + meta +
                '}';
    }
}
