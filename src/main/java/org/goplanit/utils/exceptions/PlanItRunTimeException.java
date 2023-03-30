package org.goplanit.utils.exceptions;

import java.util.Collection;

/**
 * General run time exception for PlanIt which wraps exceptions thrown during execution
 * 
 * @author markr
 *
 */
public class PlanItRunTimeException extends RuntimeException {

  /**
   * Logger for this class
   */
  private static final long serialVersionUID = 567458653348604906L;

  /**
   * Constructor using String - catches logic-driven exceptions
   * 
   * @param exceptionDescription text containing description of logic error
   */
  public PlanItRunTimeException(String exceptionDescription) {
    super(exceptionDescription);
  }
  
  /**
   * Constructor using formatted string - catches logic-driven exceptions
   * 
   * @param exceptionDescription text containing description of logic error with formatted place holders
   * @param objectArgs arguments to be added to formatted string message
   */
  public PlanItRunTimeException(String exceptionDescription, Object... objectArgs) {
    this(String.format(exceptionDescription,objectArgs));
  }  
  
  /**
   * Constructor using formatted string - catches logic-driven exceptions
   * 
   * @param exceptionDescription text containing description of logic error with formatted place holders
   * @param cause original exception cause
   * @param objectArgs arguments to be added to formatted string message
   */
  public PlanItRunTimeException(String exceptionDescription, Throwable cause, Object... objectArgs) {
    this(String.format(exceptionDescription,objectArgs), cause);
  }   

  /**
   * Constructor using Exception - wraps run-time exceptions
   * 
   * @param parentException source exception
   */
  public PlanItRunTimeException(Exception parentException) {
    super(parentException);
  }

  /**
   * Constructor using Throwable - wraps run-time exceptions with additional message
   * 
   * @param exceptionDescription message
   * @param cause original exception cause
   */
  public PlanItRunTimeException(String exceptionDescription, Throwable cause) {
    super(exceptionDescription, cause);
  }
  
  /**
   * Throw a planitException if condition is met
   * 
   * @param condition when met we throw
   * @param message for exception
   * @param objectArgs to format exception string with
   * @throws PlanItRunTimeException thrown when condition not met
   */
  public static void throwIf(boolean condition, String message, Object... objectArgs) throws PlanItRunTimeException {
    if(condition) {
      throw new PlanItRunTimeException(message, objectArgs);
    }
  }
  
  /**
   * Throw a planitException if object is null
   * 
   * @param object to test
   * @param message for exception
   * @throws PlanItRunTimeException thrown when condition not met
   */
  public static void throwIfNull(Object object, String message) throws PlanItRunTimeException {
    if(object==null) {
      throw new PlanItRunTimeException(message);
    }
  }  
  
  /**
   * Throw a PLANitException if object is null
   * 
   * @param object to test
   * @param message for exception
   * @param objectArgs to format exception string with
   * @throws PlanItRunTimeException thrown when condition not met
   */
  public static void throwIfNull(Object object, String message, Object... objectArgs) throws PlanItRunTimeException {
    throwIf(object==null, message, objectArgs);
  }

  /**
   * Throw a PLANitRunTimeException if collection is null or empty
   *
   * @param <T> concrete type of collection
   * @param collection to test
   * @param message for exception
   * @param objectArgs to format exception string with
   * @throws PlanItRunTimeException thrown when condition not met
   */
  public static <T> void throwIfNullOrEmpty(Collection<T> collection, String message, Object... objectArgs) throws PlanItRunTimeException{
    throwIf(collection==null || collection.isEmpty(), message, objectArgs);
  }

  /**
   * throw a new PLANit run time exception with a message
   * @param message to include
   */
  public static void throwNew(String message) {
    throw new PlanItRunTimeException(message);
  }
}
