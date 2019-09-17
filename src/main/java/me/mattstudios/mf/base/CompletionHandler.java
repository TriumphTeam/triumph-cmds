package me.mattstudios.mf.base;

import me.mattstudios.mf.base.components.CompletionResolver;
import me.mattstudios.mf.exceptions.InvalidCompletionIdException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletionHandler {

    private final Map<String, CompletionResolver> registeredCompletions = new HashMap<>();

    /**
     * Registers all the default completions.
     */
    CompletionHandler() {
        register("#players", input -> {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            players.sort(String.CASE_INSENSITIVE_ORDER);
            return players;
        });
        register("#empty", input -> Collections.singletonList(""));
        register("#range", input -> {
            String s = String.valueOf(input);
            if (s.equalsIgnoreCase("int") || s.equalsIgnoreCase("double") || s.equalsIgnoreCase("float"))
                return IntStream.rangeClosed(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
            if (!s.contains("-"))
                return IntStream.rangeClosed(1, Integer.parseInt(s)).mapToObj(Integer::toString).collect(Collectors.toList());
            String[] minMax = s.split("-");
            int[] range = IntStream.rangeClosed(Integer.parseInt(minMax[0]), Integer.parseInt(minMax[1])).toArray();
            List<String> rangeList = new ArrayList<>();
            for (int number : range) {
                rangeList.add(String.valueOf(number));
            }
            return rangeList;
        });
        register("#enum", input -> {
            // noinspection unchecked
            Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) input;
            List<String> values = new ArrayList<>();
            for (Enum<?> enumValue : enumCls.getEnumConstants()) {
                values.add(enumValue.name());
            }
            values.sort(String.CASE_INSENSITIVE_ORDER);
            return values;
        });
    }

    /**
     * Registers a new completion.
     *
     * @param id                 The ID of the completion to register.
     * @param completionResolver A function with the result you want.
     */
    public void register(String id, CompletionResolver completionResolver) {
        if (!id.startsWith("#"))
            throw new InvalidCompletionIdException("Could not register completion, id - " + id + " does not start with #.");
        registeredCompletions.put(id, completionResolver);
    }

    /**
     * Gets the values from the registered functions.
     *
     * @param id    The ID to get from.
     * @param input The input to base an output (normally not needed).
     * @return The string list with all the completions.
     */
    List<String> getTypeResult(String id, Object input) {
        return registeredCompletions.get(id).resolve(input);
    }

    /**
     * Checks if the ID is currently registered.
     *
     * @param id The ID to check.
     * @return The result of being registered or not.
     */
    boolean isRegistered(String id) {
        if (id.contains(":")) {
            String[] content = id.split(":");
            id = content[0];
        }
        return registeredCompletions.containsKey(id);
    }
}
