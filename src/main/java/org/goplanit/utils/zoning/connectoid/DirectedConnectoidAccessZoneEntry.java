package org.goplanit.utils.zoning.connectoid;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In addition to the #ConnectoidAccessZoneEntry we also capture access link segments and specific allowed modes
 *
 * @author markr
 *
 */
public interface DirectedConnectoidAccessZoneEntry extends ConnectoidAccessZoneEntry {

  /**
   * Check if orientation of edge segment conforms to connectoid access vertex set and existing
   * access segments
   * @param accessEdgeSegment to check
   * @return true when valid, false otherwise
   */
  public boolean isValidOrientation(EdgeSegment accessEdgeSegment);

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
   * Use another access link segment in place of one this entry holds. The one replaced is taken out first, since the
   * orientation of a new access link segment is checked against the entry's first one, which may be the one replaced.
   * Nothing happens when the entry does not hold the one to replace or already holds the other
   *
   * @param replaced access link segment to stop using
   * @param replacement access link segment to use instead
   */
  public default void replaceAccessLinkSegment(EdgeSegment replaced, EdgeSegment replacement) {
    if (!hasAccessLinkSegment(replaced) || hasAccessLinkSegment(replacement)) {
      return;
    }
    removeAccessLinkSegment(replaced);
    addAccessLinkSegment(replacement);
  }

  /**
   * Whether a link segment allows any of the modes this entry allows explicitly, so the entry can be reached over it
   * for at least one of them
   *
   * @param linkSegment to check
   * @return true when it allows at least one
   */
  public default boolean isAnyExplicitlyAllowedModeAllowedBy(LinkSegment linkSegment) {
    return getExplicitlyAllowedModes().stream().anyMatch(linkSegment::isModeAllowed);
  }

  /**
   * The modes, among the given ones, this entry allows explicitly that none of its access link segments allows any
   * more, e.g. the modes a link segment just lost that no other access link segment of the entry carries
   *
   * @param modes to check, such as the modes a link segment lost
   * @return those modes, in the order given, empty when none
   */
  public default List<Mode> findExplicitlyAllowedModesNoAccessLinkSegmentAllows(Collection<Mode> modes) {
    return modes.stream().filter(mode -> getExplicitlyAllowedModes().contains(mode) &&
        getAccessLinkSegments().stream().noneMatch(segment -> ((LinkSegment) segment).isModeAllowed(mode)))
        .collect(Collectors.toList());
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
