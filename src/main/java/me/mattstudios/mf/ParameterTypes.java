package me.mattstudios.mf;

import me.mattstudios.mf.exceptions.InvalidArgumentException;

import java.util.HashMap;
import java.util.Map;

class ParameterTypes {
    // The map of registered types.
    private final Map<Class<?>, TypeResolver> registeredTypes = new HashMap<>();

    // Registers all the types;
    public ParameterTypes() {
        register(Short.class, (t) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(short.class, (t) -> {
            try {
                return tryParseNumber(Short.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(int.class, (t) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Integer.class, (t) -> {
            try {
                return tryParseNumber(Integer.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(long.class, (t) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Long.class, (t) -> {
            try {
                return tryParseNumber(Long.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(float.class, (t) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Float.class, (t) -> {
            try {
                return tryParseNumber(Float.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(double.class, (t) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
        register(Double.class, (t) -> {
            try {
                return tryParseNumber(Double.class, String.valueOf(t));
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException();
            }
        });
    }

    public void register(Class<?> context, TypeResolver method) {
        registeredTypes.put(context, method);
    }

    public Object getTypeResult(Class<?> clss, Object object) {
        return registeredTypes.get(clss).getResolved(object);
    }

    public boolean isRegisteredType(Class<?> clss) {
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

