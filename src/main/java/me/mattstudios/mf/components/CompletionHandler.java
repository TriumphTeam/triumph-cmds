package me.mattstudios.mf.components;

import me.mattstudios.mf.exceptions.InvalidCompletionIdException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletionHandler {

    private final Map<String, CompletionResolver> registeredCompletions = new HashMap<>();

    public CompletionHandler() {
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
            if (s.equals(""))
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

    public void register(String id, CompletionResolver completionResolver) {
        if (!id.startsWith("#"))
            throw new InvalidCompletionIdException("Could not register completion, id - " + id + " does not start with #.");
        registeredCompletions.put(id, completionResolver);
    }

    public List<String> getTypeResult(String id, Object input) {
        return registeredCompletions.get(id).getResolved(input);
    }

    public boolean isRegistered(String id) {
        if (id.contains(":")) {
            String[] content = id.split(":");
            id = content[0];
        }
        return registeredCompletions.containsKey(id);
    }
}
