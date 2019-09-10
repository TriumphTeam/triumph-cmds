package me.mattstudios.mf.base.components;

import me.mattstudios.mf.exceptions.InvalidArgException;
import me.mattstudios.mf.exceptions.InvalidArgExceptionMsg;

@FunctionalInterface
public interface ParameterResolver {

    /**
     * Resolves the type of class and returns the function registered.
     *
     * @param object The object to be tested.
     * @return The result of the function.
     * @throws InvalidArgException To allow to check if an error occurred or not.
     * @throws InvalidArgExceptionMsg To allow to check if an error occurred or not and send only message.
     */
    Object getResolved(Object object) throws InvalidArgException, InvalidArgExceptionMsg;

}
