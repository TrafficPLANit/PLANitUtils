package org.planit.utils.mode;

/**
 * container class and factory methods for modes with some
 * 
 * @author markr
 *
 */
public interface Modes extends Iterable<Mode> {

  /**
   * Create and register new mode
   *
   * @param externalModeId the external mode id for the mode
   * @param name           of the mode
   * @param pcu            value for the mode
   * @return new mode created
   */
  public Mode registerNew(final long externalModeId, final String name, final double pcu);

  /**
   * Return number of registered modes
   *
   * @return number of registered modes
   */
  public int size();

  /**
   * Return a Mode by its id
   * 
   * @param id the id of the Mode
   * @return the specified mode
   * 
   */
  public Mode get(long id);

  /**
   * Collect the first registered mode
   * 
   * @return first registered mode if any
   */
  public Mode getFirst();

  /**
   * Retrieve a Mode by its external Id
   * 
   * This method has the option to convert the external Id parameter into a long value, to find the mode when mode objects use long values for external ids.
   * 
   * @param externalId    the external Id of the specified mode
   * @param convertToLong if true, the external Id is converted into a long before beginning the search
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByExternalId(Object externalId, boolean convertToLong);

  /**
   * Retrieve a Mode by its external Id
   * 
   * This method is not efficient, since it loops through all the registered modes in order to find the required time period. The equivalent method in InputBuilderListener is
   * more efficient and should be used in preference to this in Java code.
   * 
   * @param externalId the external Id of the specified mode
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByExternalId(Object externalId);
}
