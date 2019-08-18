package me.mattstudios.mf.parameters;

import me.mattstudios.mf.exceptions.InvalidArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParameterTypes {
    // The map of registered types.
    private final Map<Class<?>, TypeResolver> registeredTypes = new HashMap<>();

    // Registers all the types;
    public ParameterTypes() {
        register(Short.class, (arg, type) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                return null;
            }
        });
        register(short.class, (arg, type) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(int.class, (arg, type) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error int");
            }
        });
        register(Integer.class, (arg, type) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(long.class, (arg, type) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(Long.class, (arg, type) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(float.class, (arg, type) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(Float.class, (arg, type) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(double.class, (arg, type) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(Double.class, (arg, type) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException("error");
            }
        });
        register(String.class, (arg, type) -> {
            if (arg instanceof String) return arg;
            throw new InvalidArgumentException("error");
        });
        register(String[].class, (arg, type) -> {
            if (arg instanceof String[]) return arg;
            throw new InvalidArgumentException("error");
        });
        register(Player.class, (arg, type) -> {
            Player player = Bukkit.getServer().getPlayer(String.valueOf(arg));
            if (player != null) return player;
            throw new InvalidArgumentException("error");
        });
        register(Enum.class, (arg, type) -> {
            // noinspection unchecked
            Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) type;
            for (Enum<?> enumValue : enumCls.getEnumConstants()) {
                if (enumValue.name().equalsIgnoreCase(String.valueOf(arg))) return enumValue;
            }
            throw new InvalidArgumentException("error");
        });
        register(Enum.class, (arg, type) -> {
            // noinspection unchecked
            Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) type;
            for (Enum<?> enumVal : enumCls.getEnumConstants()) {
                if (enumVal.name().equalsIgnoreCase(String.valueOf(arg))) return enumVal;
            }
            throw new InvalidArgumentException("error");
        });
    }

    /**
     * Registers the new class type of parameters and their results.
     *
     * @param clss         The class type to be added.
     * @param typeResolver The built in method that returns the value wanted.
     */
    public void register(Class<?> clss, TypeResolver typeResolver) {
        registeredTypes.put(clss, typeResolver);
    }

    /**
     * Gets a specific type result based on a class type.
     *
     * @param clss   The class to check.
     * @param object The input object of the functional interface.
     * @param sender The console sender to send messages to.
     * @return The output object of the functional interface.
     */
    public Object getTypeResult(Class<?> clss, Object object, CommandSender sender) {
        try {
            return registeredTypes.get(clss).getResolved(object, clss);
        } catch (InvalidArgumentException e) {
            sender.sendMessage(e.getMessage());
            return null;
        }
    }

    public Object getTypeResult(Class<?> clss, Object object, CommandSender sender, Class<?> parseClass) {
        try {
            return registeredTypes.get(clss).getResolved(object, parseClass);
        } catch (InvalidArgumentException e) {
            sender.sendMessage(e.getMessage());
            return null;
        }
    }

    /**
     * Checks if the class has already been registered or not.
     *
     * @param clss The class type to check.
     * @return Returns true if it contains.
     */
    public boolean isRegisteredType(Class<?> clss) {
        return registeredTypes.containsKey(clss);
    }

    /**
     * Tries to parse a number from a string.
     *
     * @param clss   The class type of number it is.
     * @param number The number string.
     * @return The number if successfully parsed.
     * @throws NumberFormatException If can't parse it.
     */
    private Number tryParseNumber(Class<?> clss, String number) throws NumberFormatException {
        switch (clss.getName()) {

            case "java.lang.Short":
                return Short.parseShort(number);

            case "java.lang.Integer":
                return Integer.parseInt(number);

            case "java.lang.Long":
                return Long.parseLong(number);

            case "java.lang.Float":
                return Float.parseFloat(number);

            case "java.lang.Double":
                return Double.parseDouble(number);

            default:
                throw new NumberFormatException();

        }
    }

    public static List<String> enumNames(Enum<?>[] values) {
        return Stream.of(values).map(Enum::name).collect(Collectors.toList());
    }
}

