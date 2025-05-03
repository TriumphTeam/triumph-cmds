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

    /**
     * Registers an argument type with the given argument resolver.
     *
     * @param clazz    The {@link Class} type of the argument to be registered.
     * @param resolver The {@link ArgumentResolver} used for resolving the argument type.
     */
    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull ArgumentResolver<S> resolver) {
        registryContainer.getArgumentRegistry().register(clazz, resolver);
    }

    /**
     * Registers an argument type with its corresponding factory.
     *
     * @param clazz   The {@link Class} type of the argument to be registered.
     * @param factory The {@link InternalArgument.Factory} used for creating the argument type.
     */
    public final void registerArgument(final @NotNull Class<?> clazz, final @NotNull InternalArgument.Factory<S, ST> factory) {
        registryContainer.getArgumentRegistry().register(clazz, factory);
    }

    /**
     * Registers a suggestion resolver using the specified key and the default suggestion method.
     * These are simple suggestions using {@link String} only values.
     *
     * @param key      The {@link SuggestionKey} used to identify the suggestion resolver in the suggestion registry.
     * @param resolver The {@link SuggestionResolver.Simple} used to resolve suggestions in the form of {@link String} values.
     */
    public void registerSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver.Simple<S> resolver) {
        registerSuggestion(key, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    /**
     * Registers a suggestion resolver using the specified key, suggestion method,
     * and a simple suggestion resolver.
     *
     * @param key      The {@link SuggestionKey} used to identify the suggestion resolver in the suggestion registry.
     * @param method   The {@link SuggestionMethod} defining how suggestions should be filtered or matched.
     * @param resolver The {@link SuggestionResolver.Simple} used to resolve suggestions in the form of {@link String} values.
     */
    public void registerSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver.Simple<S> resolver
    ) {
        registryContainer.getSuggestionRegistry().register(key, resolver, method, suggestionMapper);
    }

    /**
     * Registers a rich suggestion resolver using the specified key and the default suggestion method.
     * Rich suggestions are based on the {@link ST} type which depends on the platform.
     *
     * @param key      The {@link SuggestionKey} used to identify the suggestion resolver in the suggestion registry.
     * @param resolver The {@link SuggestionResolver} used to resolve suggestions in the form of rich suggestion values.
     */
    public void registerRichSuggestion(final @NotNull SuggestionKey key, final @NotNull SuggestionResolver<S, ST> resolver) {
        registerRichSuggestion(key, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    /**
     * Registers a rich suggestion resolver using the specified key, suggestion method, and resolver.
     * Rich suggestions are based on the {@link ST} type, which depends on the platform.
     *
     * @param key      The {@link SuggestionKey} used to identify the suggestion resolver in the suggestion registry.
     * @param method   The {@link SuggestionMethod} defining how suggestions should be filtered or matched.
     * @param resolver The {@link SuggestionResolver} used to resolve suggestions in the form of rich suggestion values.
     */
    public void registerRichSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver<S, ST> resolver
    ) {
        registryContainer.getSuggestionRegistry().registerRich(key, resolver, method, suggestionMapper);
    }

    /**
     * Registers a static list of suggestions using the specified suggestion key and the default suggestion method.
     * On platforms like Discord, this is equivalent to the "Choice" system.
     *
     * @param key          The {@link SuggestionKey} used to identify the suggestions in the suggestion registry.
     * @param suggestions  The {@link List} of static suggestions to be registered.
     */
    public void registerStaticSuggestion(final @NotNull SuggestionKey key, final @NotNull List<String> suggestions) {
        registerStaticSuggestion(key, commandOptions.getDefaultSuggestionMethod(), suggestions);
    }

    /**
     * Registers a static list of suggestions with the specified key, suggestion method, and suggestion values.
     * The suggestions are associated with the provided key and will be resolved using the given suggestion method
     * in the suggestion registry.
     * On platforms like Discord, this is equivalent to the "Choice" system.
     *
     * @param key          The {@link SuggestionKey} used to identify the suggestions in the suggestion registry.
     * @param method       The {@link SuggestionMethod} defining how suggestions should be filtered or matched.
     * @param suggestions  The {@link List} of static suggestions to be registered.
     */
    public void registerStaticSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull List<String> suggestions
    ) {
        registryContainer.getSuggestionRegistry().registerStatic(key, suggestions, method, suggestionMapper);
    }

    /**
     * Registers a static list of rich suggestions using the specified suggestion key and the default suggestion method.
     * Rich suggestions are based on the {@link ST} type, which is platform-dependent.
     * On platforms like Discord, this is equivalent to the "Choice" system.
     *
     * @param key         The {@link SuggestionKey} used to identify the rich suggestions in the suggestion registry.
     * @param suggestions The {@link List} of static rich suggestions to be registered.
     */
    public void registerStaticRichSuggestion(final @NotNull SuggestionKey key, final @NotNull List<ST> suggestions) {
        registerStaticRichSuggestion(key, commandOptions.getDefaultSuggestionMethod(), suggestions);
    }

    /**
     * Registers a static list of rich suggestions using the specified suggestion key, suggestion method,
     * and a list of suggestion values. Rich suggestions are based on the {@link ST} type, which is platform-dependent.
     * On platforms like Discord, this is equivalent to the "Choice" system.
     *
     * @param key          The {@link SuggestionKey} used to identify the rich suggestions in the suggestion registry.
     * @param method       The {@link SuggestionMethod} defining how suggestions should be filtered or matched.
     * @param suggestions  The {@link List} of static rich suggestions to be registered.
     */
    public void registerStaticRichSuggestion(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionMethod method,
            final @NotNull List<ST> suggestions
    ) {
        registryContainer.getSuggestionRegistry().registerStaticRich(key, suggestions, method, suggestionMapper);
    }

    /**
     * Registers a suggestion resolver with the given argument type and the default suggestion method.
     *
     * @param type     The {@link Class} type that the suggestion resolver is associated with.
     * @param resolver The {@link SuggestionResolver.Simple} used for resolving suggestions in the form of {@link String} values.
     */
    public void registerSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver.Simple<S> resolver) {
        registerSuggestion(type, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    /**
     * Registers a suggestion for the specified type with the provided suggestion method
     * and resolver.
     *
     * @param type the class type for which the suggestion is being registered.
     * @param method the method defining the suggestion behavior.
     * @param resolver The {@link SuggestionResolver.Simple} used for resolving suggestions in the form of {@link String} values.
     */
    public void registerSuggestion(
            final @NotNull Class<?> type,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionResolver.Simple<S> resolver
    ) {
        registryContainer.getSuggestionRegistry().register(type, resolver, method, suggestionMapper);
    }

    /**
     * Registers a rich suggestion for a specific type using the provided suggestion resolver.
     *
     * This method binds the given type to the specified suggestion resolver, allowing
     * for customized suggestion resolution for the designated type.
     * Rich suggestions are based on the {@link ST} type, which depends on the platform.
     *
     * @param type the class type for which the rich suggestion is being registered.
     * @param resolver The {@link SuggestionResolver} used to resolve suggestions in the form of rich suggestion values.
     */
    public void registerRichSuggestion(final @NotNull Class<?> type, final @NotNull SuggestionResolver<S, ST> resolver) {
        registerRichSuggestion(type, commandOptions.getDefaultSuggestionMethod(), resolver);
    }

    /**
     * Registers a rich suggestion for the specified type using the provided method and resolver.
     * Rich suggestions are based on the {@link ST} type, which depends on the platform.
     *
     * @param type the class type for which the rich suggestion is being registered.
     * @param method the method defining the suggestion behavior.
     * @param resolver The {@link SuggestionResolver} used to resolve suggestions in the form of rich suggestion values.
     */
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
