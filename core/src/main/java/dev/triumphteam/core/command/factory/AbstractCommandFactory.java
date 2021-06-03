package dev.triumphteam.core.command.factory;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.Command;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandFactory<C extends Command> {

    private String name;
    private final List<String> alias = new ArrayList<>();

    protected AbstractCommandFactory(@NotNull final BaseCommand baseCommand) {
        extractCommandNames(baseCommand);
    }

    /**
     * Abstract method so children can handle the return of the new {@link Command}.
     *
     * @return A {@link Command} implementation.
     */
    @NotNull
    protected abstract C create();

    /**
     * Used for the child factories to get the command name.
     *
     * @return The command name.
     */
    @NotNull
    protected String getName() {
        return name;
    }

    /**
     * Used for the child factories to get a {@link List<String>} with the command's alias.
     *
     * @return The command alias.
     */
    @NotNull
    protected List<String> getAlias() {
        return alias;
    }

    /**
     * Helper method for getting the command names from the command annotation.
     *
     * @param baseCommand The {@link BaseCommand} instance.
     * @throws CommandRegistrationException In case something goes wrong should throw exception.
     */
    private void extractCommandNames(final BaseCommand baseCommand) throws CommandRegistrationException {
        final Class<? extends BaseCommand> commandClass = baseCommand.getClass();
        final dev.triumphteam.core.annotations.Command commandAnnotation = AnnotationUtil.getAnnotation(commandClass, dev.triumphteam.core.annotations.Command.class);

        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or `@Command` annotation missing", commandClass);
            }

            this.name = commandName;
            this.alias.addAll(baseCommand.getAlias());
        } else {
            this.name = commandAnnotation.value();
            Collections.addAll(this.alias, commandAnnotation.alias());
        }

        this.alias.addAll(baseCommand.getAlias());

        if (this.name.isEmpty()) {
            throw new CommandRegistrationException("Command name is empty!", commandClass);
        }
    }

}
