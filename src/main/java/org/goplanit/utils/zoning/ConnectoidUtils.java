package org.goplanit.utils.zoning;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.physical.Link;
import org.locationtech.jts.geom.Point;

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
  public static Stream<DirectedConnectoid> filterDirectedConnectoidsReferencingLinks(
      final Collection<? extends Link> links, Stream<DirectedConnectoid> directedConnectoidStream) {
  /* find all directed connectoids with link segments that have matching downstream locations if sink or
       or upstream locations if source connectoids*/
    return directedConnectoidStream.filter(dc ->
        links.stream().anyMatch(l -> {
      if(l.hasLinkSegmentAb() && dc.getAccessVertex().equals(l.getVertexB()) &&
          dc.isAccessNodeDownstreamOfSegments()){
        /* sink match */
        return true;
      }
      if(l.hasLinkSegmentBa() && dc.getAccessVertex().equals(l.getVertexA()) &&
          dc.isAccessNodeUpstreamOfSegments()){
        /* source match */
        return true;
      }
      return false;
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
  public static Set<DirectedConnectoid> findDirectedConnectoidsReferencingLinks(
          Collection<MacroscopicLink> links, Stream<DirectedConnectoid> directedConnectoidStream) {
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
  public static <C extends Connectoid<?>, Z extends Zone> void updateAccessZoneMapping(
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
