package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;

import java.util.Collection;
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

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, Mode... allowedModes) {
    if(!hasAccessZoneEntry(zone)){
      createAccessZoneEntry(zone);
    }
    getAccessZoneEntry(zone).addAllowedModes(allowedModes);
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, Collection<Mode> allowedModes) {
    if(!hasAccessZoneEntry(zone)){
      createAccessZoneEntry(zone);
    }
    getAccessZoneEntry(zone).addAllowedModes(allowedModes);
  }

  /**
   * Check if mode is allowed for access zone
   *
   * @param accessZone to check
   * @param mode to check
   * @return true when allowed, false otherwise
   */
  public abstract boolean isModeAllowed(Zone accessZone, Mode mode);

  /**
   * Verify if any of the provided modes is allowed on the access zone connectoid combination
   *
   * @param accessZone to check
   * @param modes to check
   * @return true if success, false otherwise
   */
  public default boolean isAnyModeAllowed(Zone accessZone, Collection<Mode> modes){
    return modes.stream().anyMatch(m -> isModeAllowed(accessZone, m));
  }

  /**
   * Verify which of provided modes is allowed on the access zone connectoid combination
   *
   * @param accessZone to check
   * @param modes to check
   * @return allowed modes subset (if any)
   */
  public default Collection<Mode> getAllowedModesFrom(Zone accessZone, Collection<Mode> modes){
    return getAccessZoneEntry(accessZone).getAllowedModesFrom(modes);
  }

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

  /** set if the node access is downstream or not
   * 
   * @param nodeAccessDownstream true to set it downstream, false otherwise
   */
  public abstract void setNodeAccessDownstream(boolean nodeAccessDownstream);  
  
  /** determine if the node access is downstream or not from perspective of access segments
   * 
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public abstract boolean isAccessNodeAlwaysDownstream();

  /** determine if the node access is upstream or not from perspective of access segments
   *
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public default boolean isAccessNodeAlwaysUpstream(){
    return !isAccessNodeAlwaysDownstream();
  }
    
  
  /** the class for directed connectoid id generation
   * 
   * @return class to use
   */
  public default Class<DirectedConnectoid> getDirectedConnectoidIdClass(){
    return DIRECTED_CONNECTOID_ID_CLASS;
  }


}
