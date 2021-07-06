package org.planit.utils.network.layer.macroscopic;

import org.planit.utils.id.ManagedIdEntities;

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
       
  /** collect the first entry that would be returned by the iterator
   * @return first entry
   */
  public abstract MacroscopicLinkSegmentType getFirst();

}
