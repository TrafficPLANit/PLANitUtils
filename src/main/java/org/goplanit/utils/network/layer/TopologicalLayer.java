package org.goplanit.utils.network.layer;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.modifier.TopologicalModifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An topological layer represents a layer suited for a number of modes that is topologically meaningful without enforcing the actual implemantation of
 * this topology. IT only assumes that the topology can be modified by an underlying modifier that is accessible to the user. 
 * Since it is topologically meaningful it can have a coordinate reference system as well.
 * 
 * @author markr
 *
 */
public interface TopologicalLayer extends TransportLayer {

  /**
   * transform all underlying geometries in the layer from the given crs to the new crs
   * 
   * @param fromCoordinateReferenceSystem presumed current crs
   * @param toCoordinateReferenceSystem   to tranform to crs
   * @throws PlanItException thrown if error
   */
  public abstract void transform(CoordinateReferenceSystem fromCoordinateReferenceSystem, CoordinateReferenceSystem toCoordinateReferenceSystem) throws PlanItException;


  /** Provide access to the modifier options for this layer
   * 
   * @return layer modifier
   */
  public abstract TopologicalModifier getLayerModifier();

}