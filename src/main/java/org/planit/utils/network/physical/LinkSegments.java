package org.planit.utils.network.physical;

import org.planit.utils.graph.EdgeSegments;

/**
 * wrapper around EdgeSegments interface to support LinkSegments explicitly rather than EdgeSegments
 * 
 * @author markr
 *
 * @param <LS> link segment type
 */
public interface LinkSegments<LS extends LinkSegment> extends EdgeSegments<LS> {
    
  /**
   * Retrieve a link segment by its external Id
   * 
   * This method is not efficient, since it loops through all the registered link segments in order to find the required link segment. 
   * TODO: currently utilised in Python wrapper via get_by_external_id as well as for feature in initial costs that can be provided by external id,
   * this should be refactored such that this is no longer possible, or be made more efficient.
   * 
   * @param externalId the external Id of the specified link segment type
   * @return the retrieved link segment, or null if no link segment type was found
   */
  public LS getByExternalId(String externalId);
}
