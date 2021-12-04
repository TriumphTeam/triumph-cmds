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

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.jda.annotation.Privileges;
import dev.triumphteam.cmd.jda.annotation.Roles;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Processor for Slash JDA platform specific code.
 *
 * @param <S> The sender type.
 */
final class SlashCommandProcessor<S> extends AbstractCommandProcessor<S, SlashSender> {

    private final List<Long> enabledRoles = new ArrayList<>();
    private final List<Long> disabledRoles = new ArrayList<>();

    public SlashCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        super(baseCommand, argumentRegistry, requirementRegistry, messageRegistry, senderMapper);
        extractPrivilege();
    }

    public List<Long> getEnabledRoles() {
        return enabledRoles;
    }

    public List<Long> getDisabledRoles() {
        return disabledRoles;
    }

    private void extractPrivilege() {
        final List<Roles> roles = getRolesFromAnnotations(getAnnotatedClass());
        if (roles.isEmpty()) return;

        for (final Roles role : roles) {
            for (final long id : role.value()) {
                if (role.disabled()) {
                    disabledRoles.add(id);
                    continue;
                }

                enabledRoles.add(id);
            }
        }
    }

    private List<Roles> getRolesFromAnnotations(@NotNull final Class<?> klass) {
        final Privileges privileges = klass.getAnnotation(Privileges.class);
        if (privileges != null) return Arrays.asList(privileges.value());

        final Roles roles = klass.getAnnotation(Roles.class);
        if (roles != null) return Collections.singletonList(roles);
        return Collections.emptyList();
    }

}