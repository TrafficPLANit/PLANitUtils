package org.planit.utils.event;

/** Priority that a listener can claim upon registration for events. The higher the claimed priority the earlier the listener
 * is invoked comapred to lower priority calls.
 * 
 * @author markr
 *
 */
public enum EventListenerPriority {

  HIGH,
  REGULAR,
  LOW;
  
  /** Verify if the priority is the lowest priority available
   * 
   * @param priority to verify
   * @return true when lowest, false otherwise
   */
  public boolean isLowest() {
    return this.equals(LOW);
  }

  /** Verify if the priority is the highest priority available
   * 
   * @param priority to verify
   * @return true when lowest, false otherwise
   */  
  public boolean isHighest() {
    return this.equals(HIGH);
  }
}
