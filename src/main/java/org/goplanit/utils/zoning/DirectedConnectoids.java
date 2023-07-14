package org.goplanit.utils.zoning;


import org.goplanit.utils.misc.IterableUtils;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoids deepCloneWithMapping(BiConsumer<DirectedConnectoid, DirectedConnectoid> mapper);

  /**
   * For a given physical network layers container that has a relation with these directed connectoids, index all connectoids by a custom key on the layers they
   * connect to.
   *
   * @param <L> type of the layer
   * @param <K> type of comparable
   * @param networkLayers to index for
   * @param mapToKey the mapping to key from connectoid
   * @return directed connectoids indexed by access node location per layer
   */
  public default <L extends UntypedPhysicalLayer<?,?,?>, K extends Comparable> Map<L, Map<K, List<DirectedConnectoid>>> groupByPhysicalLayerAndCustomKey(
      Iterable<L> networkLayers, Function<DirectedConnectoid,K> mapToKey){
    Map<L,Map<K, List<DirectedConnectoid>>> directedConnectoidsByLocation = new HashMap<>();
    for(var dirConnectoid : this){
      var accessLinkSegment = dirConnectoid.getAccessLinkSegment();
      var layerOption = IterableUtils.asStream(networkLayers).filter(l -> l.getLinkSegments().get(accessLinkSegment.getId())!=null).findFirst();
      if(!layerOption.isPresent()){
        continue;
      }

      directedConnectoidsByLocation.putIfAbsent(layerOption.get(), new HashMap<>());
      var layerConnectoids = directedConnectoidsByLocation.get(layerOption.get());
      layerConnectoids.putIfAbsent(mapToKey.apply(dirConnectoid),new ArrayList<>(1));
      layerConnectoids.get(mapToKey.apply(dirConnectoid)).add(dirConnectoid);
    }
    return directedConnectoidsByLocation;
  }
  
}
