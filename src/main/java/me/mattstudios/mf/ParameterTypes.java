package me.mattstudios.mf;

import me.mattstudios.mf.exceptions.InvalidArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

class ParameterTypes {
    // The map of registered types.
    private final Map<Class<?>, TypeResolver> registeredTypes = new HashMap<>();

    // Registers all the types;
    ParameterTypes() {
        register(Short.class, (arg, player) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                player.sendMessage("test");
                return null;
            }
        });
        register(short.class, (arg, player) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(int.class, (arg, player) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                player.sendMessage("test");
                return null;
            }
        });
        register(Integer.class, (arg, player) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(long.class, (arg, player) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Long.class, (arg, player) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(float.class, (arg, player) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Float.class, (arg, player) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(double.class, (arg, player) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Double.class, (arg, player) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(arg));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(String.class, (arg, player) -> arg instanceof String ? arg : new InvalidArgumentException());
        register(String[].class, (arg, player) -> arg instanceof String[] ? arg : new InvalidArgumentException());
        register(Player.class, (arg, player) -> {
            return Bukkit.getServer().getPlayer(String.valueOf(arg)) != null ? Bukkit.getServer().getPlayer(String.valueOf(arg)) : new InvalidArgumentException();
        });
    }

    // Allows people to register their own types.
    void register(Class<?> context, TypeResolver method) {
        registeredTypes.put(context, method);
    }

    // Gets the type result.
    Object getTypeResult(Class<?> clss, Object object, Player player) {
        System.out.println("on get");
        System.out.println(registeredTypes.get(clss).getResolved(object, player).getClass().isInterface());
        System.out.println("on get end");
        return registeredTypes.get(clss).getResolved(object, player);
    }

    boolean isRegisteredType(Class<?> clss) {
        return registeredTypes.containsKey(clss);
    }

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

