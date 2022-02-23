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
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.slash.annotation.Choices;
import dev.triumphteam.cmd.slash.choices.Choice;
import dev.triumphteam.cmd.slash.choices.ChoiceKey;
import dev.triumphteam.cmd.slash.choices.ChoiceRegistry;
import dev.triumphteam.cmd.slash.choices.EmptyChoice;
import dev.triumphteam.cmd.slash.choices.EnumChoice;
import dev.triumphteam.cmd.slash.choices.SimpleChoice;
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
final class SlashSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {

    private final ChoiceRegistry choiceRegistry;

    private final List<Choice> choices;

    public SlashSubCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final String parentName,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final ChoiceRegistry choiceRegistry,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(baseCommand, parentName, method, argumentRegistry, requirementRegistry, messageRegistry, senderValidator);
        this.choiceRegistry = choiceRegistry;
        this.choices = extractChoices(method, baseCommand.getClass());
    }

    @Override
    protected List<BiConsumer<Boolean, InternalArgument<S, ?>>> getArgValidations() {
        return Collections.singletonList(validateLimitless());
    }

    @NotNull
    public List<Choice> getChoices() {
        return choices;
    }


    public List<Choice> extractChoices(
            @NotNull final Method method,
            @NotNull final Class<? extends BaseCommand> commandClass
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
            @NotNull final Method method,
            @NotNull final List<Choice> choiceList,
            @NotNull final Class<? extends BaseCommand> commandClass
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
            @NotNull final List<Choice> choiceList,
            final int index,
            @Nullable final Choice choice
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

    private List<dev.triumphteam.cmd.slash.annotation.Choice> getChoicesFromAnnotation(@NotNull final Method method) {
        final Choices requirements = method.getAnnotation(Choices.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.slash.annotation.Choice suggestion = method.getAnnotation(dev.triumphteam.cmd.slash.annotation.Choice.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }

}
