package org.goplanit.utils.network.layer.macroscopic.intersection;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Factory for intersections. An intersection can be created without registering it, completed with raw edits, and
 * registered in its container afterwards, so the container indexes it whole.
 *
 * @author markr
 */
public interface IntersectionFactory extends ManagedIdEntityFactory<Intersection> {

  /**
   * Create an intersection with a single member node, without registering it on the container
   *
   * @param node        first member node
   * @param controlType of the intersection
   * @param type        first kind of the intersection
   * @return created intersection
   */
  public abstract Intersection create(Node node, IntersectionControlType controlType, IntersectionType type);

  /**
   * Register an intersection created by this factory on its container, which indexes it whole
   *
   * @param intersection to register
   * @return the registered intersection
   */
  public abstract Intersection register(Intersection intersection);

  /**
   * Create an intersection with a single member node and register it on the container
   *
   * @param node        member node
   * @param controlType of the intersection
   * @param type        kind of the intersection
   * @return created and registered intersection
   */
  public abstract Intersection registerNew(Node node, IntersectionControlType controlType, IntersectionType type);
}
