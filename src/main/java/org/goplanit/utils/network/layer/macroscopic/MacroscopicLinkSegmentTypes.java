package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.mode.Mode;

/**
 * A container interface for macroscopic link segment types
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypes extends ManagedIdEntities<MacroscopicLinkSegmentType> {
   
  /**
   * Return a MacroscopicLinkSegmentType by its Xml id
   * 
   * @param xmlId the XML id of the MacroscopicLinkSegmentType
   * @return the specified MacroscopicLinkSegmentType instance
   */
  public abstract MacroscopicLinkSegmentType getByXmlId(String xmlId);  
         
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypeFactory getFactory();


  /**
   * Convenience method to determine the maximum speed limit across all types for a given mode
   *
   * @param mode to use
   * @return found maximum applied speed limit
   */
  public default double findMaximumSpeedLimit(Mode mode){
    double maxSpeedLimitKmH = Double.NEGATIVE_INFINITY;
    for(var linkSegmentType : this) {
      if(linkSegmentType.isModeAllowed(mode)){
        double maxSpeedCurr = linkSegmentType.getMaximumSpeedKmH(mode);
        if(maxSpeedCurr > maxSpeedLimitKmH ){
          maxSpeedLimitKmH = maxSpeedCurr;
        }
      }
    }
    return maxSpeedLimitKmH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes deepClone();

}
