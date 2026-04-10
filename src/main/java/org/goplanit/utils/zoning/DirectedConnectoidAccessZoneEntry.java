package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;

import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * In addition to the #ConnectoidAccessZoneEntry we also capture access link segments and specific allowed modes
 *
 * @author markr
 *
 */
public interface DirectedConnectoidAccessZoneEntry extends ConnectoidAccessZoneEntry {

  /**
   * add another access link segment for this connectoid zone combination (it is assumed compatible
   * with existing ones, i.e. they all end/start at the same access node
   *
   * @param accessEdgeSegment to add
   * @return true when success
   */
  public abstract boolean addAccessLinkSegment(EdgeSegment accessEdgeSegment);

  /**
   * remove an access link segment for this connectoid zone combination
   *
   * @param accessEdgeSegment to remove
   * @return true when success
   */
  public abstract boolean removeAccessLinkSegment(EdgeSegment accessEdgeSegment);

  /** The edge segment that provides access
   *
   * @return access edge segments
   */
  public abstract Collection<? extends EdgeSegment> getAccessLinkSegments();

  /**
   * Obtain first access segment if available
   *
   * @return segment
   */
  public default EdgeSegment getFirstAccessLinkSegment(){
    return hasAccessLinkSegments() ? getAccessLinkSegments().iterator().next() : null;
  }

  /** Verify if an access link segment is present
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegments() {
    return getAccessLinkSegments()!=null && !getAccessLinkSegments().isEmpty();
  }

  /**
   * Check if access segment exists
   *
   * @param accessSegment to check
   * @return true if present false otherwise
   */
  public default boolean hasAccessLinkSegment(EdgeSegment accessSegment){
    return getAccessLinkSegments().contains(accessSegment);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoidAccessZoneEntry shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoidAccessZoneEntry deepClone();

}
