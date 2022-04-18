package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory to create conjugate link segments and register them on its container
 * 
 * @author markr
 *
 */
public interface ConjugateLinkSegmentFactory extends GraphEntityFactory<ConjugateLinkSegment>{
  
  /**
   * Create conjugate link segment
   *
   * @param parent  the parent of this conjugate segment
   * @param directionAb direction of travel
   * @return the created segment
   */
  public ConjugateLinkSegment create(final ConjugateLink parent, final boolean directionAb);

  /**
   * Create conjugate link segment and register it
   *
   * @param parent            the parent of this conjugate segment
   * @param directionAb           direction of travel
   * @param registerOnNodeAndLink option to register the new conjugate segment on the underlying conjugate link and its conjugate nodes
   * @return the created segment
   */
  public ConjugateLinkSegment registerNew(ConjugateLink parent, boolean directionAb, boolean registerOnNodeAndLink);
  


}
