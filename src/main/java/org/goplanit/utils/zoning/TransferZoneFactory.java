package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface TransferZoneFactory extends ManagedIdEntityFactory<TransferZone>{

  /**
   * Create and register new transfer zone
   *
   * @return the new zone created
   */
  public abstract TransferZone registerNew();

  /**
   * Register a new transfer zone without registering and set its XML id and type
   *
   * @param type transfer zone type to use
   * @param syncXmlIdToId when true, set the XML id to the underlying internal id, when false, so not set XML id yet
   * @return the new zone created
   */
  public abstract TransferZone registerNew(TransferZoneType type, boolean syncXmlIdToId);

  /**
   * Create a new transfer zone without registering
   *
   * @return the new zone created
   */
  public abstract TransferZone createNew();

  /**
   * Create a new transfer zone without registering and set its XML id and type
   *
   * @param type transfer zone type to use
   * @param syncXmlIdToId when true, set the XML id to the underlying internal id, when false, so not set XML id yet
   * @return the new zone created
   */
  public default TransferZone createNew(TransferZoneType type, boolean syncXmlIdToId) {
    var transferZone = createNew();

    /* type */
    transferZone.setType(type);

    /* xml id */
    if (syncXmlIdToId){
      transferZone.setXmlId(String.valueOf(transferZone.getId()));
    }

    return transferZone;
  }
}
