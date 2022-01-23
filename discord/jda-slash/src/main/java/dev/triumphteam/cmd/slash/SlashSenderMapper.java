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
package dev.triumphteam.cmd.slash;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Simple mapper than returns itself.
 */
class SlashSenderMapper implements SenderMapper<SlashSender, SlashSender> {

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Set<Class<? extends SlashSender>> getAllowedSenders() {
        return ImmutableSet.of(SlashSender.class);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public SlashSender map(@NotNull final SlashSender defaultSender) {
        return defaultSender;
    }

}
