package org.goplanit.utils.network.layer;

import org.goplanit.utils.graph.GraphEntity;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.modifier.UntypedDirectedGraphLayerModifier;

import java.util.function.Consumer;

/**
 * Untyped directed graph layer comprising containers with custom entity types. Use this as a base template for typed
 * implementations and derived interfaces. No access to containers is dictated, so derived interfaces are free to
 * define suitable method names to access the underlying containers and/or entities. As all entities are id managed,
 * access to the id token used by this layer is required
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayer<V extends DirectedVertex, E extends DirectedEdge, S extends EdgeSegment>
        extends TopologicalLayer {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayer<V,E,S> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayer<V,E,S> deepClone();

  /**
   * Collect the id grouping token used for all entities registered on the layer, i.e., this layer's specific
   * identifier for generating ids unique and contiguous within this
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
