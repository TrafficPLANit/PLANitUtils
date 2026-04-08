package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.Collection;
import java.util.Optional;

/**
 * In addition to the #ConnectoidAccessZoneEntry we also capture access link segments and specific allowed modes
 *
 * @author markr
 *
 */
public interface DirectedConnectoidAccessZoneEntry extends ConnectoidAccessZoneEntry {

  /** the class ot use for id generation */
  public static final Class<DirectedConnectoidAccessZoneEntry> CONNECTOID_ID_CLASS =
          DirectedConnectoidAccessZoneEntry.class;

  /**
   * add another access link segment for this connectoid zone combination (it is assumed compatible
   * with existing ones, i.e. they all end/start at the same access node
   *
   * @param accessEdgeSegment to add
   */
  public abstract boolean addAccessLinkSegment(LinkSegment accessEdgeSegment);

  /**
   * remove an access link segment for this connectoid zone combination
   *
   * @param accessEdgeSegment to remove
   */
  public abstract boolean removeAccessLinkSegment(LinkSegment accessEdgeSegment);

  /** The edge segment that provides access
   *
   * @return access edge segment
   */
  public abstract Collection<LinkSegment> getAccessLinkSegments();

  /**
   * Replace the access link segment for this connectoid
   *
   * @param accessEdgeSegment to use
   */
  public abstract void replaceAccessLinkSegment(LinkSegment accessEdgeSegment);

  /** Verify if an access link segment is present
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegments() {
    return getAccessLinkSegments()!=null && !getAccessLinkSegments().isEmpty();
  }


  /** Add an allowed mode.
   *
   * @param allowedMode to add
   */
  public abstract void addAllowedMode(Mode allowedMode);

  /** Verify if a mode is allowed access to the zone via this connectoid
   *
   * @param mode to verify if allowed
   * @return true when allowed, false otherwise
   */
  public abstract boolean isModeAllowed(Mode mode);

  /** Verify if any of the modes is allowed access to the zone via this connectoid
   *
   * @param accessZone to verify
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
   * @param accessZone to check
   * @return the modes explicitly allowed for this zone, null if none
   */
  public abstract Collection<Mode> getExplicitlyAllowedModes();

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   * 
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  public default void addAllowedModes(Mode... allowedModes) {
    for(int index = 0 ; index < allowedModes.length; ++index) {
      addAllowedMode(allowedModes[index]);
    }
  }
  
  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   * 
   * @param transferZone to add allowed mode(s) to
   * @param allowedModes to add
   */  
  public default void addAllowedModes(Collection<Mode> allowedModes) {
    allowedModes.forEach(this::addAllowedMode);
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
   * @param accessZone to check
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
