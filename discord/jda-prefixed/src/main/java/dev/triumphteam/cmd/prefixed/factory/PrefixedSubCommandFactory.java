/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.prefixed.factory;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.SimpleSubCommand;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.factory.AbstractSubCommandFactory;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.prefixed.sender.PrefixedCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class PrefixedSubCommandFactory extends AbstractSubCommandFactory<PrefixedCommandSender, SimpleSubCommand<PrefixedCommandSender>> {

    public PrefixedSubCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<PrefixedCommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<PrefixedCommandSender> requirementRegistry,
            @NotNull final MessageRegistry<PrefixedCommandSender> messageRegistry
    ) {
        super(baseCommand, method, argumentRegistry, requirementRegistry, messageRegistry);
    }

    @Nullable
    @Override
    public SimpleSubCommand<PrefixedCommandSender> create(@NotNull final String parentName) {
        if (getName() == null) return null;
        return new SimpleSubCommand<>(this, parentName);
    }

    @Override
    protected void extractArguments(@NotNull final Method method) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            if (i == 0) {
                if (!PrefixedCommandSender.class.isAssignableFrom(parameter.getType())) {
                    throw createException("Invalid or missing sender parameter (must be a PrefixedCommandSender).");
                }
                continue;
            }

            createArgument(parameter);
        }
    }

}
