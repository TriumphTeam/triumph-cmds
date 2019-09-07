package me.mattstudios.mf.components;

import me.mattstudios.mf.exceptions.InvalidArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ParameterHandler {

    // The map of registered parameters.
    private final Map<Class<?>, parameterResolver> registeredTypes = new HashMap<>();

    // Registers all the parameters;
    public ParameterHandler() {
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
    }

    /**
     * Registers the new class type of parameters and their results.
     *
     * @param clss         The class type to be added.
     * @param parameterResolver The built in method that returns the value wanted.
     */
    public void register(Class<?> clss, parameterResolver parameterResolver) {
        registeredTypes.put(clss, parameterResolver);
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

    /**
     * Special case for enums because it wasn't working the other way.
     *
     * @param clss       The class, should be Enum.class.
     * @param object     The object to test.
     * @param sender     The Player to send errors messages.
     * @param parseClass The main class to get results from.
     * @return The enum value or error.
     */
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
}

