package org.planit.utils.zoning;

/**
 * A zone where transfers between different network layers may occur. Trips do not
 * terminate at transfer zones
 * 
 * @author markr
 *
 */
public interface TransferZone extends Zone {

  /** In addition to a zone id across all zones of any derived type, each Od zone also has a unique id
   * across the Od zones specifically
   * @return
   */
  public long getTransferZoneId();

  /** set the type of this transfer zone
   * 
   * @param transferZoneType to set
   */  
  public void setTransferZoneType(TransferZoneType transferZoneType);
  
  /** collect the type of this transfer zone
   * 
   * @return transfer zone type
   */
  public TransferZoneType getTransferZoneType();
}