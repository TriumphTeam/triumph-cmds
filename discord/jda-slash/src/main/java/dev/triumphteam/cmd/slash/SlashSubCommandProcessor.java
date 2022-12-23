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
import dev.triumphteam.cmd.core.command.argument.InternalArgument;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.processor.OldAbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.slash.annotation.Choices;
import dev.triumphteam.cmd.slash.choices.Choice;
import dev.triumphteam.cmd.slash.choices.ChoiceKey;
import dev.triumphteam.cmd.slash.choices.ChoiceRegistry;
import dev.triumphteam.cmd.slash.choices.EmptyChoice;
import dev.triumphteam.cmd.slash.choices.EnumChoice;
import dev.triumphteam.cmd.slash.choices.SimpleChoice;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Processor for Slash JDA platform specific code.
 *
 * @param <S> The sender type.
 */
final class SlashSubCommandProcessor<S> extends OldAbstractSubCommandProcessor<S> {

    private final ChoiceRegistry choiceRegistry;
    private final AttachmentRegistry attachmentRegistry;

    private final List<Choice> choices;

    public SlashSubCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull String parentName,
            final @NotNull Method method,
            final @NotNull SlashRegistryContainer<S> registryContainer,
            final @NotNull SenderValidator<S> senderValidator
    ) {
        super(baseCommand, parentName, method, registryContainer, senderValidator);
        this.choiceRegistry = registryContainer.getChoiceRegistry();
        this.attachmentRegistry = registryContainer.getAttachmentRegistry();
        this.choices = extractChoices(method, baseCommand.getClass());
    }

    @Override
    protected @NotNull List<@NotNull BiConsumer<@NotNull Boolean, @NotNull InternalArgument<S, ?>>> getArgValidations() {
        return Collections.singletonList(validateLimitless());
    }

    public @NotNull List<@NotNull Choice> getChoices() {
        return choices;
    }

    @Override
    protected @NotNull InternalArgument<S, String> createSimpleArgument(
            final @NotNull Class<?> type,
            final @NotNull String parameterName,
            final @NotNull String argumentDescription,
            final @NotNull Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        if (type == Message.Attachment.class) {
            return new AttachmentArgument<>(
                    ((SlashRegistryContainer<S>) getRegistryContainer()).getAttachmentRegistry(),
                    parameterName,
                    argumentDescription,
                    type,
                    suggestion,
                    position,
                    optional
            );
        }

        return super.createSimpleArgument(type, parameterName, argumentDescription, suggestion, position, optional);
    }

    public @NotNull List<@NotNull Choice> extractChoices(
            final @NotNull Method method,
            final @NotNull Class<? extends BaseCommand> commandClass
    ) {
        final List<Choice> choiceList = new ArrayList<>();

        for (final dev.triumphteam.cmd.slash.annotation.Choice choice : getChoicesFromAnnotation(method)) {
            final String key = choice.value();
            if (key.isEmpty()) {
                choiceList.add(new EmptyChoice());
                continue;
            }

            final Supplier<List<String>> resolver = choiceRegistry.getChoiceResolver(ChoiceKey.of(key));

            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + key + "`", method, commandClass);
            }

            choiceList.add(new SimpleChoice(resolver));
        }

        extractSuggestionFromParams(method, choiceList, commandClass);
        return choiceList;
    }

    private void extractSuggestionFromParams(
            final @NotNull Method method,
            final @NotNull List<@NotNull Choice> choiceList,
            final @NotNull Class<? extends BaseCommand> commandClass
    ) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();

            final dev.triumphteam.cmd.slash.annotation.Choice choice = parameter.getAnnotation(dev.triumphteam.cmd.slash.annotation.Choice.class);
            final String choiceKey = choice == null ? "" : choice.value();

            final int addIndex = i - 1;
            if (choiceKey.isEmpty() || choiceKey.equals("enum")) {
                if (Enum.class.isAssignableFrom(type)) {
                    //noinspection unchecked
                    setOrAdd(choiceList, addIndex, new EnumChoice((Class<? extends Enum<?>>) type));
                    continue;
                }

                setOrAdd(choiceList, addIndex, null);
                continue;
            }

            final Supplier<List<String>> resolver = choiceRegistry.getChoiceResolver(ChoiceKey.of(choiceKey));
            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the choice key `" + choiceKey + "`", method, commandClass);
            }
            setOrAdd(choiceList, addIndex, new SimpleChoice(resolver));
        }
    }

    private void setOrAdd(
            final @NotNull List<@NotNull Choice> choiceList,
            final int index,
            final @Nullable Choice choice
    ) {
        if (index >= choiceList.size()) {
            if (choice == null) {
                choiceList.add(new EmptyChoice());
                return;
            }
            choiceList.add(choice);
            return;
        }

        if (choice == null) return;
        choiceList.set(index, choice);
    }

    private List<dev.triumphteam.cmd.slash.annotation.Choice> getChoicesFromAnnotation(final @NotNull Method method) {
        final Choices requirements = method.getAnnotation(Choices.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.slash.annotation.Choice suggestion = method.getAnnotation(dev.triumphteam.cmd.slash.annotation.Choice.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }
}
