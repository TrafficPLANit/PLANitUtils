package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A directed connectoid is referring to one or more access edge segments in a network (layer) which is directed
 * for access hence, the connectoid is also being directed. It is used in situations where not all segments
 * connected to the access node may be available to access the connectoid and may be also allowed only for certain modes
 *
 * @author markr
 *
 */
public interface DirectedConnectoid extends Connectoid<DirectedConnectoidAccessZoneEntry>{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<DirectedConnectoid> DIRECTED_CONNECTOID_ID_CLASS = DirectedConnectoid.class;

  /** Collect the directed connectoid id
   * 
   * @return directed connectoid id
   */
  public abstract long getDirectedConnectoidId();

  /**
   * Will collate and produce a stream of all access link segments across all its access
   * zone entries
   *
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getAccessLinkSegmentsStream(){
    return getAccessZoneEntries().values().stream().flatMap(e -> e.getAccessLinkSegments().stream());
  }

  /**
   * Check if any access edge segments are registered for any access zone
   *
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegments(){
    return hasAccessZoneEntries() && getAccessZoneEntries().values().stream().anyMatch(
        DirectedConnectoidAccessZoneEntry::hasAccessLinkSegments);
  }

  /**
   * Find first available access link segment across all access zones (if any)
   *
   * @return first found as optional
   */
  private Optional<? extends EdgeSegment> getFirstAccessLinkSegment() {
    return getAccessLinkSegmentsStream().findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoid shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoid deepClone();


  /** determine if the node access is downstream or not from perspective of access segments
   * 
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public abstract boolean isAccessNodeDownstreamOfSegments();

  /** determine if the node access is upstream or not from perspective of access segments
   *
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public default boolean isAccessNodeUpstreamOfSegments(){
    return !isAccessNodeDownstreamOfSegments();
  }
    
  
  /** the class for directed connectoid id generation
   * 
   * @return class to use
   */
  public default Class<DirectedConnectoid> getDirectedConnectoidIdClass(){
    return DIRECTED_CONNECTOID_ID_CLASS;
  }


}
