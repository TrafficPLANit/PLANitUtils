package org.planit.utils.network.physical;

import org.planit.utils.graph.EdgeSegments;

/**
 * wrapper around EdgeSegments interface to support LinkSegments explicitly rather than EdgeSegments
 * 
 * @author markr
 *
 * @param <LS>
 */
public interface LinkSegments<LS extends LinkSegment> extends EdgeSegments<LS> {

  
  /**
   * Find a LinkSegment by the external Ids of its start and end nodes
   * TODO: only used PLANit initial costs parser, functinoality should be moved there.
   *
   * @param startId reference to start node
   * @param endId   reference to end node
   * @return the linkSegment found
   */
  public LS getByStartAndEndNodeId(final long startId, final long endId);
  
  /** option to collect by external id. 
   * 
   * TODO: Legacy code should be  refactored, only used in Python wrapper
   * 
   * @param externalId to find
   * @param convertToLong convert to long if true
   * @return entity if present
   */
  public LS getByExternalId(Object externalId, boolean convertToLong);

  /**
   * Retrieve a link segment by its external Id
   * 
   * This method is not efficient, since it loops through all the registered link segments in order to find the required link segment. 
   * TODO: currently only utilised in Python wrapper via get_by_external_id, this should be refactored somehow
   * 
   * @param externalId the external Id of the specified link segment type
   * @return the retrieved link segment, or null if no link segment type was found
   */
  public LS getByExternalId(Object externalId);
}
