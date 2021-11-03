package org.goplanit.utils.function;

import org.goplanit.utils.exceptions.PlanItException;

/**
 * Allows for a functional interface that throws PlanitExceptions
 * 
 * @author markr
 *
 * @param <T> type parameter
 * @param <R> return type
 */
@FunctionalInterface
public interface PlanitExceptionFunction<T, R> {
   R apply(T t) throws PlanItException;
}
 