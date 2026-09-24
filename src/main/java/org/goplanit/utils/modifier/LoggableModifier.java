package org.goplanit.utils.modifier;

/**
 * A modifier that reports what it changed as it goes, which a caller keeping its own account of the same changes can
 * turn off so that the two are not both stated.
 *
 * @author markr
 */
public interface LoggableModifier {

  /** by default a modifier states what it changed, there being nothing else to state it */
  public static final boolean DEFAULT_LOG_MODIFICATIONS = true;

  /**
   * Verify whether the modifier states what it changes
   *
   * @return true when stated, false otherwise
   */
  public abstract boolean isLogModifications();

  /**
   * Set whether the modifier states what it changes
   *
   * @param logModifications to set
   */
  public abstract void setLogModifications(boolean logModifications);
}
