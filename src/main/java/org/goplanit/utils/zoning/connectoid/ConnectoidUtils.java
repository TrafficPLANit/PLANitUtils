package org.goplanit.utils.zoning.connectoid;

import org.goplanit.utils.zoning.Zone;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Utility functions around connectoids
 *
 * @author markr
 */
public class ConnectoidUtils {

  /** Dummy not to be instantiated */
  private ConnectoidUtils(){}

  /** Logger to use */
  private static final Logger LOGGER = Logger.getLogger(ConnectoidUtils.class.getCanonicalName());

  /**
   * Update the access zone entries' access zone for the connectoids based on the mapping provided (if any)
   *
   * @param <C> type connectoid
   * @param <Z> type of zone
   * @param connectoids to update
   * @param zoneToZoneMapping to use, should contain original zone as currently used and then the value is
   *                          the new zone to replace it
   * @param removeMissingMappings when true if there is no mapping then the existing zone is nullified, otherwise it is
   *                              left in-tact
   */
  public static <C extends Connectoid, Z extends Zone> void updateAccessZoneMapping(
          Iterable<C> connectoids, Function<Z, Z> zoneToZoneMapping, boolean removeMissingMappings) {
    for(var connectoid :  connectoids){
      if(!connectoid.hasAccessZoneEntries()){
        continue;
      }

      for(var accessZoneEntryIter = connectoid.iterator();accessZoneEntryIter.hasNext();) {
        var currAccessZoneEntry = accessZoneEntryIter.next();
        var newAccessZone = zoneToZoneMapping.apply((Z) currAccessZoneEntry.getAccessZone());
        if(newAccessZone == null && removeMissingMappings){
          accessZoneEntryIter.remove();
        }else if(newAccessZone != null){
          currAccessZoneEntry.setAccessZone(newAccessZone);
        }
      }
      // in case the new mapped zone has a different id we need to redo id mapping as well
      connectoid.recreateAccessZoneIdMapping();
    }
  }
}
