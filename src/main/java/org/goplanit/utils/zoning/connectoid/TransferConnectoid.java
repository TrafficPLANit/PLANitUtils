package org.goplanit.utils.zoning.connectoid;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.zoning.Zone;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A directed connectoid is referring to one or more zone-type-mode combination entries such as pt vehicle stops
 * that explicitly define eligible (directed) access edge segments (disallowing all others), therefore this
 * connectoid is considered directed.
 * <p>
 *   this does not preclude the connected from having non-directed entries where all segments are eligible but this
 *   would then be for a subset of other zone-type-mode combinations such as traveller access
 * </p>
 *
 * @author markr
 *
 */
public interface TransferConnectoid extends Connectoid{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<TransferConnectoid> TRANSFER_CONNECTOID_ID_CLASS = TransferConnectoid.class;

  /** Collect the directed connectoid id
   * 
   * @return directed connectoid id
   */
  public abstract long getTransferConnectoidId();

  /** Add a new access zone entry with default properties for a given usage type which is of the directed type
   *
   * @param zone to register as accessible
   * @param type to use
   * @return overwritten zone if any
   */
  public abstract DirectedConnectoidAccessZoneEntry createDirectedAccessZoneEntry(Zone zone, ZoneConnectoidType type);

  /** Add a new access zone entry with default properties for a given usage type which is of the directed type
   *
   * @param zone to register as accessible
   * @param type to use
   * @param accessSegment to use
   * @return overwritten zone if any
   */
  public abstract DirectedConnectoidAccessZoneEntry createDirectedAccessZoneEntry(
      Zone zone, ZoneConnectoidType type, EdgeSegment accessSegment);

  /**
   * Obtain as directed access entry if it is, if it is not or does not exist null is returned
   * @param accessZone to use
   * @param type to use
   * @return result found
   */
  public default DirectedConnectoidAccessZoneEntry getAsDirectedAccessZoneEntry(Zone accessZone, ZoneConnectoidType type){
    var entry = getAccessZoneEntry(accessZone, type);
    if(entry == null){
      return null;
    }
    return (entry instanceof DirectedConnectoidAccessZoneEntry) ? (DirectedConnectoidAccessZoneEntry) entry : null;
  }

  /**
   * Will collate and produce a stream of all explicitly listed unique access link segments across all its access
   * zone entries and types (that are directed, as undirected entries are ignored here).
   *
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getExplicitAccessLinkSegmentsStream(){
    return getExplicitAccessLinkSegmentsStream(e -> true);
  }

  /**
   * Will collate and produce a stream of all unique access link segments across all its access
   * zone entries and types
   *
   * @param entriesFilter to apply to the zoneConnectoidEntries
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getExplicitAccessLinkSegmentsStream(
      Predicate<DirectedConnectoidAccessZoneEntry> entriesFilter){
    return getAccessZoneEntriesByType().values().stream().flatMap(e-> e.values().stream()).
        filter(e -> e instanceof DirectedConnectoidAccessZoneEntry).map(
            e -> ((DirectedConnectoidAccessZoneEntry)e)).filter(entriesFilter).flatMap(
                e -> e.getAccessLinkSegments().stream()).distinct();
  }

  /**
   * Will collate and produce a stream of all explicitly references access link segments across all its access
   * zone entries
   *
   * @param type type specifier
   * @return access link segments
   */
  public default Stream<? extends EdgeSegment> getExplicitAccessLinkSegmentsStream(ZoneConnectoidType type){
    return getExplicitAccessLinkSegmentsStream(e -> e.getType().equals(type));
  }

  /** Verify if access zones entries exist for given type
   * @return true when present, false otherwise
   */
  public default boolean hasAccessZoneEntries(ZoneConnectoidType type) {
    return getNumberOfAccessZoneEntries()>0 && getAccessZoneEntriesStream(type).findFirst().isPresent();
  }

  /**
   * Verify if given link segment is present on this connectoid for any type, mode, or access zone combination
   * @param linkSegment to check
   * @return true when found, false otherwise
   */
  public default boolean hasAccessLinkSegment(LinkSegment linkSegment){
    return getExplicitAccessLinkSegmentsStream().anyMatch(ls -> ls.equals(linkSegment));
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
    return getExplicitAccessLinkSegmentsStream().findFirst();
  }

  /**
   * Find first available access link segment across all access zones (if any)
   *
   * @param type type specifier
   * @return first found as optional
   */
  private Optional<? extends EdgeSegment> getFirstAccessLinkSegment(ZoneConnectoidType type) {
    return getExplicitAccessLinkSegmentsStream(type).findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferConnectoid shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferConnectoid deepClone();

  /** the class for directed connectoid id generation
   * 
   * @return class to use
   */
  public default Class<TransferConnectoid> getDirectedConnectoidIdClass(){
    return TRANSFER_CONNECTOID_ID_CLASS;
  }


}
