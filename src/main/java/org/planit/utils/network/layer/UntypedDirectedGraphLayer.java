package org.planit.utils.network.layer;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;
import org.planit.utils.id.IdGroupingToken;
import org.planit.utils.network.layer.UntypedDirectedGraphLayer;
import org.planit.utils.network.layer.modifier.UntypedDirectedGraphLayerModifier;

/**
 * Network layer comprising of containers with custom entity types. Use this as a base template for implementations and derived interfaced that
 * are typed. No access to containers is dictated to allow for maximum flexibility for derived interface to define suitable method names to access
 * underlying containers and/or entities. Since all entities are id managed we do require access to the id token used by this layer
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayer<V extends DirectedVertex, E extends DirectedEdge, S extends EdgeSegment> extends TopologicalLayer {
  
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
