package dev.triumphteam.cmd.core;

import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.message.ContextualKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class ManagerSetup<D, S, O extends CommandOptions<D, S, O, ST>, ST> {

    private final RegistryContainer<D, S, ST> registryContainer;
    private final SuggestionMapper<ST> suggestionMapper;
    private final O commandOptions;

    public ManagerSetup(final @NotNull RegistryContainer<D, S, ST> registryContainer, final @NotNull O commandOptions) {
        this.registryContainer = registryContainer;
        this.commandOptions = commandOptions;
        this.suggestionMapper = commandOptions.getCommandExtensions().getSuggestionMapper();
    }

    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull ArgumentResolver<S> resolver) {
        registryContainer.getArgumentRegistry().register(clazz, resolver);
    }

    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull InternalArgument.Factory<S, ST> factory) {
        registryContainer.getArgumentRegistry().register(clazz, factory);
    }

    public void registerSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver.Simple<S> resolver) {
        registerSuggestion(key, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    public void registerSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver.Simple<S> resolver
    ) {
        registryContainer.getSuggestionRegistry().register(key, resolver, method, suggestionMapper);
    }

    public void registerRichSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver<S, ST> resolver) {
        registerRichSuggestion(key, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    public void registerRichSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver<S, ST> resolver
    ) {
        registryContainer.getSuggestionRegistry().registerRich(key, resolver, method, suggestionMapper);
    }

    public void registerStatiSuggestion(final @NotNull SuggestionKey key, final @NotNull List<String> suggestions) {
        registerStatiSuggestion(key, commandOptions.getDefaultSuggestionMethod(), suggestions);
    }

    public void registerStatiSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull List<String> suggestions
    ) {
        registryContainer.getSuggestionRegistry().registerStatic(key, suggestions, method, suggestionMapper);
    }

    public void registerStaticRichSuggestion(final @NotNull SuggestionKey key, final @NotNull List<ST> suggestions) {
        registerStaticRichSuggestion(key, commandOptions.getDefaultSuggestionMethod(), suggestions);
    }

    public void registerStaticRichSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull List<ST> suggestions
    ) {
        registryContainer.getSuggestionRegistry().registerStaticRich(key, suggestions, method, suggestionMapper);
    }

    public void registerSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver.Simple<S> resolver) {
        registerSuggestion(type, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    public void registerSuggestion(
            final @NotNull Class<?> type,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver.Simple<S> resolver
    ) {
        registryContainer.getSuggestionRegistry().register(type, resolver, method, suggestionMapper);
    }

    public void registerRichSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver<S, ST> resolver) {
        registerRichSuggestion(type, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    public void registerRichSuggestion(
            final @NotNull Class<?> type,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver<S, ST> resolver
    ) {
        registryContainer.getSuggestionRegistry().registerRich(type, resolver, method, suggestionMapper);
    }

    /**
     * Registers a list of arguments to be used as named arguments in a command.
     *
     * @param key       The {@link ArgumentKey} to represent the list.
     * @param arguments The list of arguments.
     */
    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull Argument @NotNull ... arguments) {
        registerNamedArguments(key, Arrays.asList(arguments));
    }

    /**
     * Registers a list of arguments to be used on a {@link Arguments} argument in a command.
     *
     * @param key       The {@link ArgumentKey} to represent the list.
     * @param arguments The {@link List} of arguments.
     */
    public final void registerNamedArguments(final @NotNull ArgumentKey key, final @NotNull List<Argument> arguments) {
        getRegistryContainer().getNamedArgumentRegistry().register(key, arguments);
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key   The {@link FlagKey} to represent the list.
     * @param flags The list of flags.
     */
    public final void registerFlags(final @NotNull FlagKey key, final @NotNull Flag @NotNull ... flags) {
        registerFlags(key, Arrays.asList(flags));
    }

    /**
     * Registers a list of flags to be used on a {@link Flags} argument or {@link Arguments} argument, in a command.
     *
     * @param key   The {@link FlagKey} to represent the list.
     * @param flags The {@link List} of flags.
     */
    public final void registerFlags(final @NotNull FlagKey key, final @NotNull List<Flag> flags) {
        getRegistryContainer().getFlagRegistry().register(key, flags);
    }

    /**
     * Registers a custom message.
     *
     * @param key      The {@link ContextualKey} of the message to be registered.
     * @param resolver The {@link ArgumentResolver} with the message sending resolution.
     */
    public final <C extends MessageContext> void registerMessage(
            final @NotNull ContextualKey<C> key,
            final @NotNull MessageResolver<S, C> resolver
    ) {
        getRegistryContainer().getMessageRegistry().register(key, resolver);
    }

    /**
     * Registers a requirement.
     *
     * @param key      The {@link RequirementKey} of the requirement to be registered.
     * @param resolver The {@link ArgumentResolver} with the requirement resolution.
     */
    public final void registerRequirement(
            final @NotNull RequirementKey key,
            final @NotNull RequirementResolver<D, S> resolver
    ) {
        getRegistryContainer().getRequirementRegistry().register(key, resolver);
    }

    protected final @NotNull RegistryContainer<D, S, ST> getRegistryContainer() {
        return registryContainer;
    }

    protected final @NotNull O getCommandOptions() {
        return commandOptions;
    }
}
