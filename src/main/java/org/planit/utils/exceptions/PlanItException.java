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
   * @param exceptionDescription
   *          text containing description of logic error
   */
  public PlanItException(String exceptionDescription) {
    super(exceptionDescription);
  }

  /**
   * Constructor using Exception - wraps run-time exceptions
   * 
   * @param parentException
   *          source exception
   */
  public PlanItException(Exception parentException) {
    super(parentException);
  }

  public PlanItException(String exceptionDescription, Throwable cause) {
    super(exceptionDescription, cause);
  }
  
  /**
   * Throw a planitException if condition is met
   * 
   * @param message
   * @throws PlanItException
   */
  public static void throwIf(boolean condition, String message) throws PlanItException {
    if(condition) {
      throw new PlanItException(message);
    }
  }
}
