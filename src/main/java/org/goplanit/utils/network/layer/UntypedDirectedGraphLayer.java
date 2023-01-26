package org.goplanit.utils.network.layer;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.modifier.UntypedDirectedGraphLayerModifier;

/**
 * Network layer comprising containers with custom entity types. Use this as a base template for implementations and derived interfaced that
 * are typed. No access to containers is dictated to allow for maximum flexibility for derived interface to define suitable method names to access
 * underlying containers and/or entities. Since all entities are id managed we do require access to the id token used by this layer
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayer<V extends DirectedVertex, E extends DirectedEdge, S extends EdgeSegment> extends TopologicalLayer {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayer clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayer deepClone();

  /**
   * Collect the id grouping token used for all entities registered on the layer, i.e., this layer's specific identifier for generating ids unique and contiguous within this
   * layer 
   * 
   * @return the layer id grouping token
   */
  public abstract IdGroupingToken getLayerIdGroupingToken();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayerModifier<V,E,S> getLayerModifier();  
      
}
