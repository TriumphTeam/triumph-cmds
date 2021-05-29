package dev.triumphteam.core.internal.command.factory;

import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.internal.BaseCommand;
import dev.triumphteam.core.internal.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandFactory<C extends Command> {

    private String commandName;
    private final List<String> alias = new ArrayList<>();

    protected AbstractCommandFactory(@NotNull final BaseCommand baseCommand) {
        extractCommandNames(baseCommand);
    }

    public abstract C create();

    protected String getCommandName() {
        return commandName;
    }

    protected List<String> getAlias() {
        return alias;
    }

    /**
     * Helper method for getting the command names from the command annotation
     *
     * @param baseCommand The {@link BaseCommand} instance
     * @throws CommandRegistrationException In case something goes wrong should throw exception
     */
    private void extractCommandNames(final BaseCommand baseCommand) throws CommandRegistrationException {
        final Class<? extends BaseCommand> commandClass = baseCommand.getClass();
        final dev.triumphteam.core.annotations.Command commandAnnotation = AnnotationUtil.getAnnotation(commandClass, dev.triumphteam.core.annotations.Command.class);

        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or `@Command` annotation missing", commandClass);
            }

            this.commandName = commandName;
            this.alias.addAll(baseCommand.getAlias());
        } else {
            this.commandName = commandAnnotation.value();
            Collections.addAll(this.alias, commandAnnotation.aliases());
        }

        this.alias.addAll(baseCommand.getAlias());

        if (this.commandName.isEmpty()) {
            throw new CommandRegistrationException("Command name is empty!", commandClass);
        }
    }

}
