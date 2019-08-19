package me.mattstudios.mf.components;

import java.util.List;

@FunctionalInterface
interface CompletionResolver {

    /**
     * Resolves the command completion added.
     *
     * @param input An input from the completion event.
     * @return A string list with all the completion values.
     */
    List<String> resolve(Object input);

}
