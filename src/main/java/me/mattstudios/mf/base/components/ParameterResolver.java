package me.mattstudios.mf.base.components;

@FunctionalInterface
public interface ParameterResolver {

    /**
     * Resolves the type of class and returns the function registered.
     *
     * @param object The object to be tested.
     * @return The result of the function.
     */
    Object[] getResolved(Object object);

}
