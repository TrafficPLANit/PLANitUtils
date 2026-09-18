package org.goplanit.utils.zoning.connectoid;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.zoning.Zone;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  /** find all directed connectoids from the provided container indexed by location that share an access node with
   * the passed in links' link segments
   *
   * @param links to find referencing directed connectoids for
   * @param directedConnectoidStream connectoids to filter on
   * @return all identified directed connectoids
   */
  public static Stream<TransferConnectoid> filterDirectedConnectoidsReferencingLinks(
      final Collection<? extends Link> links, Stream<TransferConnectoid> directedConnectoidStream) {

    /* find all directed connectoids with link segments that  overlap with any of the link its link segments */
    return directedConnectoidStream.filter(dc ->
        links.stream().anyMatch(l -> {
          if(!l.getVertexB().equals(dc.getReferenceVertex()) && !l.getVertexA().equals(dc.getReferenceVertex())){
            return false;
          }

          return dc.getExplicitAccessLinkSegmentsStream().anyMatch(ls ->
              ls.equals(l.getLinkSegmentAb()) || ls.equals(l.getLinkSegmentBa()));
    }));
  }

  /**
   * Collect all connectoids when their access link segments reside on the provided links.
   *
   * @param links to collect connectoid information for, i.e., only connectoids referencing link segments
   *              with a parent link in this collection
   * @param directedConnectoidStream all connectoids to consider
   * @return found connectoids, connectoids
   */
  public static Set<TransferConnectoid> findDirectedConnectoidsReferencingLinks(
          Collection<MacroscopicLink> links, Stream<TransferConnectoid> directedConnectoidStream) {
    var filteredDirectedConnectoidStream =
          ConnectoidUtils.filterDirectedConnectoidsReferencingLinks(links,directedConnectoidStream);
    return filteredDirectedConnectoidStream.collect(Collectors.toSet());
  }

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
