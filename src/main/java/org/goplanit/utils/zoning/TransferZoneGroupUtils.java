package org.goplanit.utils.zoning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * Utility functions around transfer zone groups
 *
 * @author markr
 */
public class TransferZoneGroupUtils {

  /**
   * Update the transfer zones of all transfer zone groups based on the mapping provided (if any)
   *
   * @param <G> type of transfer zone group
   * @param <Z> type of transfer zone
   * @param transferZoneGroups to apply to
   * @param zoneToZoneMapping to use should contain original transfer zone and then the value is the new transfer zone to replace it
   * @param removeMissingMappings when true if there is no mapping, the transferZone is removed from the group, otherwise it is left in-tact
   */
  public static <G extends TransferZoneGroup, Z extends TransferZone> void updateTransferZoneMapping(Iterable<G> transferZoneGroups, Function<Z, Z> zoneToZoneMapping, boolean removeMissingMappings) {
    for(var tzGroup :  transferZoneGroups){
      if(!tzGroup.hasTransferZones()){
        continue;
      }

      Collection<TransferZone> transferZonesToAdd = new ArrayList<>(tzGroup.getTransferZones().size());
      for(var tzGroupIter = tzGroup.getTransferZones().iterator();tzGroupIter.hasNext();) {
        var currAccessZone = tzGroupIter.next();
        var newAccessZone = zoneToZoneMapping.apply((Z) currAccessZone);
        if (newAccessZone != null || removeMissingMappings) {
          tzGroupIter.remove();
          if(newAccessZone != null) transferZonesToAdd.add(newAccessZone);
        }
      }
      tzGroup.addAllTransferZones(transferZonesToAdd);
    }
  }
}
