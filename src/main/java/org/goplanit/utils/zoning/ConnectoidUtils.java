package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.NetworkLayer;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layers.MacroscopicNetworkLayers;
import org.goplanit.utils.network.layers.NetworkLayers;
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Utility functions around connectoids
 *
 * @author markr
 */
public class ConnectoidUtils {

  /** Logger to use */
  private static final Logger LOGGER = Logger.getLogger(ConnectoidUtils.class.getCanonicalName());

  /** find all directed connectoids from the provided container indexed by location that share an access node with the passed in link's link segments
   *
   * @param link to find referencing directed connectoids for
   * @param connectoidsByLocation connectoids to filter on
   * @return all identified directed connectoids
   */
  public static Collection<DirectedConnectoid> findDirectedConnectoidsReferencingLink(Link link, Map<Point, List<DirectedConnectoid>> connectoidsByLocation) {
    Collection<DirectedConnectoid> referencingConnectoids = new HashSet<>();
    /* find eligible locations for connectoids based on downstream locations of link segments on link */
    Set<Point> eligibleLocations = new HashSet<>();
    if(link.hasEdgeSegmentAb()) {
      eligibleLocations.add(link.getEdgeSegmentAb().getDownstreamVertex().getPosition());
    }
    if(link.hasEdgeSegmentBa()) {
      eligibleLocations.add(link.getEdgeSegmentBa().getDownstreamVertex().getPosition());
    }

    /* find all directed connectoids with link segments that have downstream locations matching the eligible locations identified*/
    for(Point location : eligibleLocations) {
      Collection<DirectedConnectoid> knownConnectoidsForLink = connectoidsByLocation.get(location);
      if(knownConnectoidsForLink != null && !knownConnectoidsForLink.isEmpty()) {
        for(DirectedConnectoid connectoid : knownConnectoidsForLink) {
          if(connectoid.getAccessLinkSegment().idEquals(link.getEdgeSegmentAb()) || connectoid.getAccessLinkSegment().idEquals(link.getEdgeSegmentBa()) ) {
            /* match */
            referencingConnectoids.add(connectoid);
          }
        }
      }
    }

    return referencingConnectoids;
  }

  /**
   * Collect all connectoids and their access node's positions if their access link segments reside on the provided links. Can be useful to ensure
   * these positions remain correct after modifying the network.
   *
   * @param links to collect connectoid information for, i.e., only connectoids referencing link segments with a parent link in this collection
   * @param connectoidsByLocation all connectoids indexed by their location
   * @return found connectoids and their accessNode position
   */
  public static Map<Point,DirectedConnectoid> findDirectedConnectoidsReferencingLinks(Collection<MacroscopicLink> links, Map<Point, List<DirectedConnectoid>> connectoidsByLocation) {
    Map<Point, DirectedConnectoid> connectoidEligibleAccessNodesLocations = new HashMap<>();
    for(Link link : links) {
      Collection<DirectedConnectoid> connectoids = ConnectoidUtils.findDirectedConnectoidsReferencingLink(link,connectoidsByLocation);
      if(connectoids !=null && !connectoids.isEmpty()) {
        connectoids.forEach( connectoid -> connectoidEligibleAccessNodesLocations.put(connectoid.getAccessNode().getPosition(),connectoid));
      }
    }
    return connectoidEligibleAccessNodesLocations;
  }

  /**
   * Update the parent edge of all edge segments based on the mapping provided (if any)
   *
   * @param <C> type od connectoid
   * @param <Z> type of zone
   * @param connectoids to update
   * @param zoneToZoneMapping to use should contain original edge as currently used on vertex and then the value is the new edge to replace it
   * @param removeMissingMappings when true if there is no mapping, the parent edge is nullified, otherwise it is left in-tact
   */
  public static <C extends Connectoid, Z extends Zone> void updateAccessZoneMapping(Iterable<C> connectoids, Function<Z, Z> zoneToZoneMapping, boolean removeMissingMappings) {
    for(var connectoid :  connectoids){
      if(!connectoid.hasAccessZones()){
        continue;
      }

      Collection<Zone> accessZonesToAdd = new ArrayList<>(connectoid.getNumberOfAccessZones());
      for(var accessZoneIter = connectoid.getAccessZones().iterator();accessZoneIter.hasNext();) {
        var currAccessZone = accessZoneIter.next();
        var newAccessZone = zoneToZoneMapping.apply((Z) currAccessZone);
        if (newAccessZone != null || removeMissingMappings) {
          accessZoneIter.remove();
          if (newAccessZone != null) accessZonesToAdd.add(newAccessZone);
        }
      }
      connectoid.addAllAccessZones(accessZonesToAdd);
    }
  }
}
