package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.Collection;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Factory for intersections of both kinds. An intersection can be created without registering it, completed with raw
 * edits, and registered in its container afterwards, so the container indexes it whole.
 *
 * @author markr
 */
public interface IntersectionFactory extends ManagedIdEntityFactory<Intersection> {

  /**
   * Create a non-spatial intersection on a node, without registering it on the container
   *
   * @param node        its node
   * @param controlType of the intersection
   * @param type        first kind of the intersection
   * @return created intersection
   */
  public abstract NonSpatialIntersection createNonSpatial(
      Node node, IntersectionControlType controlType, IntersectionType type);

  /**
   * Create a spatial intersection on several member nodes, without registering it on the container
   *
   * @param memberNodes its member nodes, two or more
   * @param controlType of the intersection
   * @param types       its kinds, at least one
   * @return created intersection
   */
  public abstract SpatialIntersection createSpatial(
      Collection<Node> memberNodes, IntersectionControlType controlType, Collection<IntersectionType> types);

  /**
   * Register an intersection created by this factory on its container, which indexes it whole
   *
   * @param intersection to register
   * @return the registered intersection
   */
  public abstract Intersection register(Intersection intersection);

  /**
   * Create a non-spatial intersection on a node and register it on the container
   *
   * @param node        its node
   * @param controlType of the intersection
   * @param type        kind of the intersection
   * @return created and registered intersection
   */
  public abstract NonSpatialIntersection registerNewNonSpatial(
      Node node, IntersectionControlType controlType, IntersectionType type);

  /**
   * Create a spatial intersection on several member nodes and register it on the container
   *
   * @param memberNodes its member nodes, two or more
   * @param controlType of the intersection
   * @param types       its kinds, at least one
   * @return created and registered intersection
   */
  public abstract SpatialIntersection registerNewSpatial(
      Collection<Node> memberNodes, IntersectionControlType controlType, Collection<IntersectionType> types);
}
