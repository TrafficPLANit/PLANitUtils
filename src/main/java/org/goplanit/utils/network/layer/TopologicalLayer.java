package org.goplanit.utils.network.layer;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.modifier.TopologicalModifier;
import org.locationtech.jts.geom.Envelope;

/**
 * A topological layer is a layer suited for a number of modes that is topologically meaningful, without enforcing how
 * this topology is implemented. It only assumes that the topology can be modified by an underlying modifier the user
 * can access. Being topologically meaningful, it can have a coordinate reference system as well.
 * 
 * @author markr
 *
 */
public interface TopologicalLayer extends NetworkLayer {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TopologicalLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TopologicalLayer deepClone();

  /**
   * transform all underlying geometries in the layer from the given crs to the new crs
   * 
   * @param fromCoordinateReferenceSystem presumed current crs
   * @param toCoordinateReferenceSystem   to transform to crs
   */
  public abstract void transform(
          CoordinateReferenceSystem fromCoordinateReferenceSystem,
          CoordinateReferenceSystem toCoordinateReferenceSystem);


  /** Provide access to the modifier options for this layer
   * 
   * @return layer modifier
   */
  public abstract TopologicalModifier getLayerModifier();

  /**
   * Create bounding box from underlying topography of the layer
   * @return envelope reflecting extreme points of bounding box (rectangular)
   */
  public abstract Envelope createBoundingBox();
}
