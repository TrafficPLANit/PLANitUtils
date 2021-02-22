package org.planit.utils.function;

import org.planit.utils.exceptions.PlanItException;

/**
 * Allows for a consumer interface that throws PlanitExceptions
 * 
 * @author markr
 *
 * @param <T> type to consume
 */
@FunctionalInterface
public interface PlanitExceptionConsumer<T> {
   void accept(T t) throws PlanItException;
}
 