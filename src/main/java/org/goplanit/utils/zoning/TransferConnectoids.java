package org.goplanit.utils.zoning;


import org.goplanit.utils.misc.IterableUtils;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * container and factory class for directed connectoids
 * 
 * @author markr
 *
 */
public interface TransferConnectoids extends Connectoids<TransferConnectoid> {

  public static final Logger LOGGER = Logger.getLogger(TransferConnectoids.class.getCanonicalName());

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract TransferConnectoidFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferConnectoids shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferConnectoids deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferConnectoids deepCloneWithMapping(BiConsumer<TransferConnectoid, TransferConnectoid> mapper);

  /**
   * For a given physical network layers container that has a relation with these directed connectoids, index all
   * connectoids by a custom key on the layers they connect to. If a connectoid has access to multiple layers
   * it will occur multiple times in the map
   *
   * @param <L> type of the layer
   * @param <K> type of comparable
   * @param networkLayers to index for
   * @param mapToKey the mapping to key from connectoid
   * @return directed connectoids indexed by key per layer
   */
  public default <L extends UntypedPhysicalLayer<?,?,?>, K extends Comparable<?>> Map<L, Map<K, List<TransferConnectoid>>>
  groupByPhysicalLayerAndCustomKey(
      Iterable<L> networkLayers, Function<TransferConnectoid,K> mapToKey){
    Map<L,Map<K, List<TransferConnectoid>>> directedConnectoidsByLocation = new HashMap<>();

    for(var dirConnectoid : this){
      var layerOptions = dirConnectoid.getExplicitAccessLinkSegmentsStream().map(ls ->
              IterableUtils.asStream(networkLayers).filter(l -> l.getLinkSegments().containsKey(ls.getId())).
                      findFirst()).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
      if(layerOptions.isEmpty()){
        continue;
      }

      for(var layer : layerOptions) {
        directedConnectoidsByLocation.putIfAbsent(layer, new HashMap<>());
        var layerConnectoids = directedConnectoidsByLocation.get(layer);
        layerConnectoids.putIfAbsent(mapToKey.apply(dirConnectoid), new ArrayList<>(1));
        layerConnectoids.get(mapToKey.apply(dirConnectoid)).add(dirConnectoid);
      }
    }
    return directedConnectoidsByLocation;
  }
  
}
