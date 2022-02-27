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
package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.jda.annotation.Privileges;
import dev.triumphteam.cmd.jda.annotation.Roles;
import dev.triumphteam.cmd.slash.choices.ChoiceRegistry;
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
final class SlashCommandProcessor<S> extends AbstractCommandProcessor<SlashSender, S> {

    private final ChoiceRegistry choiceRegistry;

    private final List<Long> enabledRoles = new ArrayList<>();
    private final List<Long> disabledRoles = new ArrayList<>();

    public SlashCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final NamedArgumentRegistry<S> namedArgumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final ChoiceRegistry suggestionRegistry,
            @NotNull final SenderMapper<SlashSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(baseCommand, argumentRegistry, namedArgumentRegistry, requirementRegistry, messageRegistry, senderMapper, senderValidator);
        this.choiceRegistry = suggestionRegistry;
        extractPrivilege();
    }

    /**
     * Gets the roles to which the command should be enabled to.
     *
     * @return The enabled roles.
     */
    @NotNull
    public List<Long> getEnabledRoles() {
        return enabledRoles;
    }

    /**
     * Gets the roles to which the command should be disabled to.
     *
     * @return The disabled roles.
     */
    @NotNull
    public List<Long> getDisabledRoles() {
        return disabledRoles;
    }

    /**
     * Gets the choice registry.
     *
     * @return The choice registry.
     */
    @NotNull
    public ChoiceRegistry getChoiceRegistry() {
        return choiceRegistry;
    }

    /**
     * Extracts the privilege data from the command.
     */
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

    /**
     * Gets the roles annotations from the class.
     *
     * @param klass The class to get from.
     * @return List with all the roles annotations.
     */
    private List<Roles> getRolesFromAnnotations(@NotNull final Class<?> klass) {
        final Privileges privileges = klass.getAnnotation(Privileges.class);
        if (privileges != null) return Arrays.asList(privileges.value());

        final Roles roles = klass.getAnnotation(Roles.class);
        if (roles != null) return Collections.singletonList(roles);
        return Collections.emptyList();
    }
}
