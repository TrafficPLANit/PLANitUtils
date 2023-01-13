package org.goplanit.utils.network.layer.modifier;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.service.ServiceLeg;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.network.layer.service.ServiceNode;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Modifier with additional functionality related to modifications to service network layers
 *
 * @author markr
 */
public interface ServiceNetworkLayerModifier<V extends ServiceNode, E extends ServiceLeg, S extends ServiceLegSegment> extends UntypedDirectedGraphLayerModifier<V,E,S> {

  /**
   * Method that will remove all entities (service nodes, legs, leg segments) that have no mapping present to the underlying physical network layer
   * the service network is attached to
   */
  public abstract void removeUnmappedServiceNetworkEntities();

}
