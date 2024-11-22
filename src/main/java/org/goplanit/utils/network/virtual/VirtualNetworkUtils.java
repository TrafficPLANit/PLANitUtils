package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;
import org.goplanit.utils.network.virtual.physical.conjugate.ConjugateConnectoidNode;
import org.goplanit.utils.zoning.OdZone;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.Zone;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for virtual networks
 *
 * @author markr
 */
public class VirtualNetworkUtils {

  /**
   * Create an inverted mapping from the centroid vertices on the conjugate nodes' original underlying
   * so the conjugate node can be collected based on such a centroid vertex
   *
   * @param conjugateVirtualLayer to use
   * @return mapping from centroid vertex (key) to conjugate connectoid node (value)
   */
  public static Map<CentroidVertex, ConjugateConnectoidNode> createCentroidVertexToConjugateNodeMapping(
      ConjugateVirtualNetworkLayer conjugateVirtualLayer) {
    var mapping = new HashMap<CentroidVertex, ConjugateConnectoidNode>();
    conjugateVirtualLayer.getVertices().stream().filter(ConjugateDirectedVertex::hasOriginalEdge).forEach(
        cn -> mapping.put(cn.getCentroidVertex(), cn));
    return mapping;
  }

  /**
   * Create a (new) mapping from zones (transfer and or OD) to their centroid vertex.
   *
   * @param virtualNetworkLayer to use for mapping
   * @param considerOdZones when true OdZones will be included in the mapping, not included otherwise
   * @param considerTransferZones when true transferZones will be included in the mapping, not included otherwise
   * @return mapping that was created
   */
  public static Map<? extends Zone, CentroidVertex> createZoneToCentroidVertexMapping(
      VirtualNetworkLayer virtualNetworkLayer, boolean considerOdZones, boolean considerTransferZones){
    return virtualNetworkLayer.getVertices().stream().filter(
        Objects::nonNull).filter(
        cv -> (considerOdZones && cv.getParent().getParentZone() instanceof OdZone) || // only those with matching zones
            (considerTransferZones && cv.getParent().getParentZone() instanceof TransferZone)).collect(
        Collectors.toMap(cv -> cv.getParent().getParentZone(), cv -> cv)); // as key value map
  }
}
