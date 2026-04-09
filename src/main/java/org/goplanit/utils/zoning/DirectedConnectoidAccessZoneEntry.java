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

  /** Add an allowed mode.
   *
   * @param allowedMode to add
   */
  public abstract void addExplicitAllowedMode(Mode allowedMode);

  /** Verify if a mode is allowed access to the zone via this connectoid
   *
   * @param mode to verify if allowed
   * @return true when allowed, false otherwise
   */
  public abstract boolean isModeAllowed(Mode mode);

  /** Verify if any of the modes is allowed access to the zone via this connectoid
   *
   * @param modes to verify if any is allowed
   * @return true when allowed, false otherwise
   */
  public default boolean isAnyModeAllowed(Collection<Mode> modes){
    return modes.stream().anyMatch(this::isModeAllowed);
  }
  
  /** collect modes that are explicitly allowed for this zone (unmodifiable). Note that if no explicit allowed
   * modes are present, all modes are implicitly allowed. When there exist explicitly allowed modes, any modes
   * in the network not included in the explicitly allowed modes are regarded to not be allowed.
   * 
   * @return the modes explicitly allowed for this zone, null if none
   */
  public abstract Collection<Mode> getExplicitlyAllowedModes();

  /**
   * Verify which of provided modes is allowed on the access zone connectoid combination
   *
   * @param modes to check
   * @return allowed modes subset (if any)
   */
  public default Collection<Mode> getAllowedModesFrom(Collection<Mode> modes){
    if(!hasExplicitlyAllowedModes()){
      return new TreeSet<>(modes);
    }else{
      return modes.stream().filter(this::isModeAllowed).collect(Collectors.toSet());
    }
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   * 
   * @param allowedModes to add
   */
  public default void addAllowedModes(Mode... allowedModes) {
    for(int index = 0 ; index < allowedModes.length; ++index) {
      addExplicitAllowedMode(allowedModes[index]);
    }
  }
  
  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   * 
   * @param allowedModes to add
   */  
  public default void addAllowedModes(Collection<Mode> allowedModes) {
    allowedModes.forEach(this::addExplicitAllowedMode);
  }

  /** Verify if any modes are allowed for this zone
   *
   * @return true when at least one mode is allowed, false otherwise
   */
  public default boolean hasExplicitlyAllowedModes() {
    Collection<Mode> allowedModes = getExplicitlyAllowedModes();
    return allowedModes!=null && !allowedModes.isEmpty();
  }

  /** Verify if all modes are allowed for this zone
   *
   * @return true when we know for certain all modes are allowed, false otherwise
   */
  public default boolean isAllModesAllowed() {
    /* no explicit allowed modes set, so all modes allowed */
    return !hasExplicitlyAllowedModes();
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
