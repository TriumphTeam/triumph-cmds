package me.mattstudios.mf.components;

import java.util.List;

@FunctionalInterface
interface CompletionResolver {

    List<String> getResolved(Object input);

}
