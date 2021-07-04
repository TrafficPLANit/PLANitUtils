package org.planit.utils.network.layer.physical;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;

public interface LinkSegmentFactory extends GraphEntityFactory<LinkSegment>{
  
  /**
   * Create link segment
   *
   * @param parentLink  the parent of this segment
   * @param directionAB direction of travel
   * @return the created segment
   * @throws PlanItException thrown if error
   */
  public LinkSegment create(final Link parentLink, final boolean directionAB) throws PlanItException;

  /**
   * Create link segment and register it
   *
   * @param parentEdge            the parent of this segment
   * @param directionAb           direction of travel
   * @param registerOnNodeAndLink option to register the new segment on the underlying link and its nodes
   * @return the created segment
   * @throws PlanItException thrown if error
   */
  public LinkSegment registerNew(Link parentLink, boolean directionAb, boolean registerOnNodeAndLink) throws PlanItException;

}
