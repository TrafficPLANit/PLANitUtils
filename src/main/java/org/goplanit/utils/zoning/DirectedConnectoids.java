package org.goplanit.utils.zoning;


import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * container and factory class for directed connectoids
 * 
 * @author markr
 *
 */
public interface DirectedConnectoids extends Connectoids<DirectedConnectoid> {

  public static final Logger LOGGER = Logger.getLogger(DirectedConnectoids.class.getCanonicalName());

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract DirectedConnectoidFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoids shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoids deepClone();

  /**
   * For a given physical network layers container that has a relatino with these directed connectoids, index all connectoids by their access node location to the physical network on the layers they
   * connect to. Do this by location rather than node, so it can be used when modifying the network and updating the node the connectoids attach to for example
   *
   * @param networkLayers to index for
   * @return directed connectoids indexed by access node location per layer
   */
  public default <L extends UntypedPhysicalLayer<?,?,?>> Map<L, Map<Point, List<DirectedConnectoid>>> indexByPhysicalLayerAndLocation(UntypedPhysicalNetworkLayers<L> networkLayers){
    Map<L,Map<Point, List<DirectedConnectoid>>> directedConnectoidsByLocation = new HashMap<>();
    for(var dirConnectoid : this){
      var accessNode = dirConnectoid.getAccessNode();
      var accessLinkSegment = dirConnectoid.getAccessLinkSegment();
      var layer = networkLayers.findFirst(l -> l.getLinkSegments().get(accessLinkSegment.getId())!=null);
      if(layer == null){
        continue;
      }

      directedConnectoidsByLocation.putIfAbsent(layer, new HashMap<>());
      var layerConnectoids = directedConnectoidsByLocation.get(layer);
      layerConnectoids.putIfAbsent(accessNode.getPosition(),new ArrayList<>(1));
      layerConnectoids.get(accessNode.getPosition()).add(dirConnectoid);
    }
    return directedConnectoidsByLocation;
  }
  
}
