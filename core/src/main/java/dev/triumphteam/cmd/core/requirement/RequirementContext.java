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
package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import org.jetbrains.annotations.NotNull;

/**
 * Holder for some context data to pass over to the {@link RequirementResolver}.
 *
 * @param <D> The default sender.
 * @param <S> The final sender.
 */
public interface RequirementContext<D, S> {

    /**
     * @return The {@link CommandMeta} of the current {@link InternalCommand}.
     */
    @NotNull CommandMeta getMeta();

    /**
     * @return The {@link SenderMapper} for reverse mapping if needed.
     */
    @NotNull SenderMapper<D, S> getSenderMapper();
}
