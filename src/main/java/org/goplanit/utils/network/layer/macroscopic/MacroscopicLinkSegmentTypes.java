package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.id.ManagedIdEntities;

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
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes clone();

}
