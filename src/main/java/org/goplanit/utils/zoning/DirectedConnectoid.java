package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.Optional;
import java.util.function.Predicate;
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
   * Will collate and produce a stream of all unique access link segments across all its access
   * zone entries and types
   *
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getAccessLinkSegmentsStream(){
    return getAccessZoneEntriesByType().values().stream().flatMap(
        e -> e.values().stream()).flatMap(
            e -> e.getAccessLinkSegments().stream()).distinct();
  }

  /**
   * Will collate and produce a stream of all unique access link segments across all its access
   * zone entries and types
   *
   * @param entriesFilter to apply to the zoneConnectoidEntries
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getAccessLinkSegmentsStream(
      Predicate<DirectedConnectoidAccessZoneEntry> entriesFilter){
    return getAccessZoneEntriesByType().values().stream().flatMap(
        initMapEntry -> initMapEntry.values().stream().filter(
            entriesFilter::test).flatMap(
                e -> e.getAccessLinkSegments().stream()).distinct());
  }

  /**
   * Will collate and produce a stream of all access link segments across all its access
   * zone entries
   *
   * @param type type specifier
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getAccessLinkSegmentsStream(ZoneConnectoidType type){
    return getAccessZoneEntriesByType().values().stream().flatMap(
        e -> e.values().stream()).filter(
            e -> e.getType().equals(type)).flatMap(
        e -> e.getAccessLinkSegments().stream()).distinct();
  }

  /**
   * Verify if given link segment is present on this connectoid for any type, mode, or access zone combination
   * @param linkSegment to check
   * @return true when found, false otherwise
   */
  public default boolean hasAccessLinkSegment(LinkSegment linkSegment){
    return getAccessLinkSegmentsStream().anyMatch(ls -> ls.equals(linkSegment));
  }

  /**
   * Check if any access edge segments are registered for any access zone
   *
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegments(){
    return getFirstAccessLinkSegment().isPresent();
  }

  /**
   * Check if any access edge segments are registered for any access zone
   *
   * @param type type specifier
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegments(ZoneConnectoidType type){
    return getFirstAccessLinkSegment(type).isPresent();
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
   * Find first available access link segment across all access zones (if any)
   *
   * @param type type specifier
   * @return first found as optional
   */
  private Optional<? extends EdgeSegment> getFirstAccessLinkSegment(ZoneConnectoidType type) {
    return getAccessLinkSegmentsStream(type).findFirst();
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
