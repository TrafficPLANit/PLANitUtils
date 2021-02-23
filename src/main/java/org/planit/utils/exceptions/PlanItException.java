package org.planit.utils.exceptions;

/**
 * General exception for PlanIt which wraps exceptions thrown during execution
 * 
 * @author markr
 *
 */
public class PlanItException extends Exception {

  /**
   * Logger for this class
   */
  private static final long serialVersionUID = 567458653348604906L;

  /**
   * Constructor using String - catches logic-driven exceptions
   * 
   * @param exceptionDescription text containing description of logic error
   */
  public PlanItException(String exceptionDescription) {
    super(exceptionDescription);
  }
  
  /**
   * Constructor using formatted string - catches logic-driven exceptions
   * 
   * @param exceptionDescription text containing description of logic error with formatted place holders
   * @param objectArgs arguments to be added to formatted string message
   */
  public PlanItException(String exceptionDescription, Object... objectArgs) {
    this(String.format(exceptionDescription,objectArgs));
  }  

  /**
   * Constructor using Exception - wraps run-time exceptions
   * 
   * @param parentException source exception
   */
  public PlanItException(Exception parentException) {
    super(parentException);
  }

  /**
   * Constructor using Throwable - wraps run-time exceptions with additional message
   * 
   * @param exceptionDescription message
   * @param cause original exception cause
   */
  public PlanItException(String exceptionDescription, Throwable cause) {
    super(exceptionDescription, cause);
  }
  
  /**
   * Throw a planitException if condition is met
   * 
   * @param condition when met we throw
   * @param message for exception
   * @throws PlanItException thrown when condition not met
   */
  public static void throwIf(boolean condition, String message) throws PlanItException {
    if(condition) {
      throw new PlanItException(message);
    }
  }
  
  /**
   * Throw a planitException if object is null
   * 
   * @param object to test
   * @param message for exception
   * @throws PlanItException thrown when condition not met
   */
  public static void throwIfNull(Object object, String message) throws PlanItException {
    if(object==null) {
      throw new PlanItException(message);
    }
  }  
}
