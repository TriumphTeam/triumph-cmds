package dev.triumphteam.cmd.slash.choices;

import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.slash.annotation.Choice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class InternalChoiceProcessor implements AnnotationProcessor<Choice> {

    private final ChoiceRegistry choiceRegistry;

    public InternalChoiceProcessor(final @NotNull ChoiceRegistry choiceRegistry) {
        this.choiceRegistry = choiceRegistry;
    }

    @Override
    public void process(
            final @NotNull Choice annotation,
            final @NotNull ProcessorTarget target,
            final @NotNull AnnotatedElement element,
            final CommandMeta.@NotNull Builder meta
    ) {
        final String keyValue = annotation.value();
        final ChoiceKey choiceKey = ChoiceKey.of(keyValue);

        if (!(element instanceof Parameter)) return;

        final Class<?> type = ((Parameter) element).getType();

        final InternalChoice internalChoice;
        if (Enum.class.isAssignableFrom(type)) {
            internalChoice = new EnumInternalChoice((Class<? extends Enum<?>>) type);
        } else {
            final Supplier<List<String>> supplier = choiceRegistry.getChoiceResolver(choiceKey);
            if (supplier == null) return;
            internalChoice = new SimpleInternalChoice(supplier);
        }

        meta.add(Choice.CHOICE_META_KEY, internalChoice);
    }
}
