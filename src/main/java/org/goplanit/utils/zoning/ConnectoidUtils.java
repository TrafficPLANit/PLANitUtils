package org.goplanit.utils.zoning;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.physical.Link;
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
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
   * the passed in link's link segments
   *
   * @param link to find referencing directed connectoids for
   * @param directedConnectoidStream connectoids to filter on
   * @return all identified directed connectoids
   */
  public static Collection<DirectedConnectoid> findDirectedConnectoidsReferencingLink(
          Link link, Stream<DirectedConnectoid> directedConnectoidStream) {

    Collection<DirectedConnectoid> referencingConnectoids = new HashSet<>();

    /* find all directed connectoids with link segments that have downstream locations matching the eligible
    locations identified*/
    directedConnectoidStream.forEach(dc -> {
      if(dc.getAccessVertex().equals(link.getVertexA()) || dc.getAccessVertex().equals(link.getVertexB())){
        /* match */
        referencingConnectoids.add(dc);
      }
    });

    return referencingConnectoids;
  }

  /**
   * Collect all connectoids and their access node's positions if their access link segments reside on the
   * provided links. Can be useful to ensure these positions remain correct after modifying the network.
   *
   * @param links to collect connectoid information for, i.e., only connectoids referencing link segments
   *              with a parent link in this collection
   * @param directedConnectoidStream all connectoids
   * @return found connectoids and their accessNode position, connectoids are directional there may be two per
   * access node
   */
  public static Map<Point,Set<DirectedConnectoid>> findDirectedConnectoidsReferencingLinks(
          Collection<MacroscopicLink> links, Stream<DirectedConnectoid> directedConnectoidStream) {
    Map<Point, Set<DirectedConnectoid>> connectoidEligibleAccessNodesLocations = new TreeMap<>();
    for(Link link : links) {
      Collection<DirectedConnectoid> connectoids =
              ConnectoidUtils.findDirectedConnectoidsReferencingLink(link,directedConnectoidStream);
      if(connectoids !=null && !connectoids.isEmpty()) {
        connectoids.forEach( connectoid -> {
          connectoidEligibleAccessNodesLocations.putIfAbsent(
              connectoid.getAccessVertex().getPosition(), new TreeSet<>());
          connectoidEligibleAccessNodesLocations.get(connectoid.getAccessVertex().getPosition()).add(
              connectoid);
        });
      }
    }
    return connectoidEligibleAccessNodesLocations;
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
