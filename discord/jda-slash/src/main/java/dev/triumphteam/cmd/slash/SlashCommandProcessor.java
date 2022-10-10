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
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.jda.annotation.Privileges;
import dev.triumphteam.cmd.jda.annotation.Roles;
import dev.triumphteam.cmd.slash.choices.ChoiceRegistry;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Processor for Slash JDA platform specific code.
 *
 * @param <S> The sender type.
 */
final class SlashCommandProcessor<S>
        extends AbstractCommandProcessor<SlashSender, S, SlashSubCommand<S>, SlashSubCommandProcessor<S>> {

    private final ChoiceRegistry choiceRegistry;

    private final List<Permission> enabledPermissions = new ArrayList<>();

    public SlashCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull SlashRegistryContainer<S> registryContainer,
            final @NotNull SenderMapper<SlashSender, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        super(baseCommand, registryContainer, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);
        this.choiceRegistry = registryContainer.getChoiceRegistry();

    }

    /**
     * Gets the permissions to which the command should be enabled to by default.
     *
     * @return The enabled permissions.
     */
    public @NotNull List<@NotNull Permission> getEnabledPermissions() {
        return enabledPermissions;
    }

    /**
     * Gets the choice registry.
     *
     * @return The choice registry.
     */
    public @NotNull ChoiceRegistry getChoiceRegistry() {
        return choiceRegistry;
    }

    /**
     * Gets the roles annotations from the class.
     *
     * @param klass The class to get from.
     * @return List with all the roles annotations.
     */
    private @NotNull List<@NotNull Roles> getRolesFromAnnotations(final @NotNull Class<?> klass) {
        final Privileges privileges = klass.getAnnotation(Privileges.class);
        if (privileges != null) return Arrays.asList(privileges.value());

        final Roles roles = klass.getAnnotation(Roles.class);
        if (roles != null) return Collections.singletonList(roles);
        return Collections.emptyList();
    }

    @Override
    protected @NotNull SlashSubCommandProcessor<S> createSubProcessor(final @NotNull AnnotatedElement method) {
        return null;
        /*return new SlashSubCommandProcessor<>(
                getBaseCommand(),
                getName(),
                method,
                (SlashRegistryContainer<S>) getRegistryContainer(),
                getSenderValidator()
        );*/
    }

    @Override
    protected @Nullable SlashSubCommand<S> createSubCommand(
            final @NotNull SlashSubCommandProcessor<S> processor,
            final @NotNull ExecutionProvider executionProvider
    ) {
        return new SlashSubCommand<>(processor, getName(), executionProvider);
    }
}
