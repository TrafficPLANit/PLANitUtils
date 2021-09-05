package org.planit.utils.network.layer.macroscopic;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.planit.utils.id.ExternalIdAble;
import org.planit.utils.id.ManagedId;
import org.planit.utils.macroscopic.MacroscopicConstants;
import org.planit.utils.mode.Mode;

/**
 * The macroscopic link segment type characteristics are contained in this class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentType extends Cloneable, ExternalIdAble, ManagedId {
  
  /** id class for generating ids */
  public static final Class<MacroscopicLinkSegmentType> MACROSCOPIC_LINK_SEGMENT_TYPE_ID_CLASS = MacroscopicLinkSegmentType.class;   
  
  /**
   * If no macroscopic link segment type is defined the default takes on an XML id of 1 
   */
  public static final String DEFAULT_XML_ID = "1";   

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentType clone();

  /**
   * Return the name of this macroscopic link segment type
   * 
   * @return the name
   */
  public abstract String getName();

  /**
   * Set the name of this macroscopic link segment type
   * 
   * @param name the name
   */
  public abstract void setName(String name);
  
  /**
   * Return the explicitly set capacity per lane of this macroscopic link segment type. This signifies a capacity
   * that is to be preferred over any inferred capacity based on physical parameters such as free speed, jam density etc. If this is set
   * any fundamental diagrams used by assignment are to be made compatible with this explicitly set capacity
   * 
   * @return capacity per lane in pcu/h/lane, null if not set
   */
  public abstract Double getExplicitCapacityPerLane();  

  /**
   * Return the explicitly defined capacity per lane of this macroscopic link segment type or the default if it has not been explicitly set
   * 
   * @return capacity per lane in pcu/h/lane or default
   */
  public default double getExplicitCapacityPerLaneOrDefault() {
    return isExplicitCapacityPerLaneSet() ? getExplicitCapacityPerLane() : MacroscopicConstants.DEFAULT_CAPACITY_PCU_HOUR_LANE;
  }
  
  /** Verify if capacity per lane is set explicitly or relies on default
   * 
   * @return true when set explicitly, false otherwise
   */
  public default boolean isExplicitCapacityPerLaneSet() {
    return getExplicitCapacityPerLane()==null;
  }

  /**
   * Return the maximum density per lane for this macroscopic link segment type. This signifies a jam density
   * that is to be preferred over any inferred density. If this is set any fundamental diagrams used by assignment are to be made 
   * compatible with this explicitly set density
   * 
   * @return the maximum density per lane in pcu/km/lane, null if not set
   */
  public abstract Double getExplicitMaximumDensityPerLane();
  
  /**
   * Return the maximum density per lane  of this macroscopic link segment type or the default if it has not been explicitly set
   * 
   * @return the maximum density per lane in pcu/km/lane
   */
  public default double getExplicitMaximumDensityPerLaneOrDefault() {
    return isExplicitMaximumDensityPerLaneSet() ? getExplicitMaximumDensityPerLane() : MacroscopicConstants.DEFAULT_MAX_DENSITY_PCU_KM_LANE;
  }
  
  /** Verify if maximum density per lane is set explicitly or relies on default
   * 
   * @return true when set explicitly, false otherwise
   */
  public default boolean isExplicitMaximumDensityPerLaneSet() {
    return getExplicitMaximumDensityPerLane()==null;
  }  

  /**
   * Set the access properties, any pre-existing access properties for modes are overwritten by the given ones
   * 
   * @param AccessProperties for one or more modes
   */
  public abstract void setAccessGroupProperties(final Collection<AccessGroupProperties> AccessProperties);
  
  /**
   * Set access properties for this link segment type, any modes with existing access properties are overwritten by the given 
   * properties.
   * 
   * @param accessProperties to set
   */
  public abstract void setAccessGroupProperties(final AccessGroupProperties accessProperties);

  /** add access group properties for the modes allowed by it. By adding instead of setting them, it is verified these properties do not yet exist, if they already exist
   * they are not registered and a warning is issued. To make sure only new group access properties are registered use {@link #findEqualAccessPropertiesForAnyMode(AccessGroupProperties)
   * 
   * @param accessProperties to register
   */  
  public abstract void addAccessGroupProperties(AccessGroupProperties accessProperties);

  /** Remove the mode properties for the passed in mode (if present)
   * 
   * @param toBeRemovedMode mode to remove properties for
   * @return true when able to remove, false otherwise
   */
  public abstract boolean removeModeAccess(final Mode toBeRemovedMode);

  /**
   * Returns the access properties for a specified mode along this link
   * 
   * @param mode the specified mode
   * @return the mode properties for this link and mode
   */
  public abstract AccessGroupProperties getAccessProperties(final Mode mode);
  
  /**
   * Verify if mode is available on type
   * 
   * @param mode to verify
   * @return available modes
   */
  public abstract boolean isModeAllowed(final Mode mode);   
  
  /**
   * return the available modes present in one of the access groups that have been registered
   * 
   * @return available modes
   */
  public abstract Set<Mode> getAllowedModes();
  
  /** Method which identifies which of the passed in modes is unavailable on the link segment
   * 
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are not available
   */
  public default Set<Mode> getDisallowedModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> !isModeAllowed(mode)).collect(Collectors.toSet());
  }

  /** Method which identifies which of the passed in modes is available on the link segment
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are available
   */
  public default Set<Mode> getAllowedModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> isModeAllowed(mode)).collect(Collectors.toSet());
  }

  /** Method which identifies which of the passed in modes is available on the link segment but not in the passed in collection of modes
   * @param modes to exclude from the available modes
   * @return collection which is a subset of the available modes, namely excludes the the passed in modes
   */
  public default Set<Mode> getAllowedModesNotIn(final Collection<Mode> modes){
    return getAllowedModes().stream().filter(mode -> !modes.contains(mode)).collect(Collectors.toSet());
  }

  /** Verify if the link segment type has any modes available
   * 
   * @return true when at least one mode is available
   */
  public default boolean hasAllowedModes() {
    return getAllowedModes()!=null && !getAllowedModes().isEmpty();
  }

  /** find group access properties that are equal to the ones that are passed in except for the allowed modes, i.e.,
   * find existing access properties for any mode that match the ones provided. IF found they are returned, otherwise null is returned
   * 
   * @param accessProperties to match against
   * @return access properties found matching, null if no match is found
   */
  public abstract AccessGroupProperties findEqualAccessPropertiesForAnyMode(AccessGroupProperties accessProperties);
  
  /** Collect the maximum speed based on the combination of the mode and any restrictions imposed by the type on this mode.
   * If the mode is not available on this type a limit of 0.0 is returned, otherwise it is the minimum speed of the mode maximum speed
   * and the restricted speed of the type for this mode
   * 
   * @param mode to use
   */
  public default Double getMaximumSpeedKmH(final Mode mode) {
    if(!isModeAllowed(mode)) {
      return 0.0;
    }
    return getAccessProperties(mode).getMaximumSpeedOrDefaultKmH(mode.getMaximumSpeedKmH());
  }
  
  /** Collect the critical speed based on the combination of the mode and any restrictions imposed by the type on this mode.
   * If the mode is not available on this type null is returned, otherwise it is the minimum speed of the mode maximum speed
   * and the restricted critical speed of the type for this mode
   * 
   * @param mode to use
   */
  public default Double getCriticalSpeedKmH(final Mode mode) {
    if(!isModeAllowed(mode)) {
      return null;
    }
    return getAccessProperties(mode).getCriticalSpeedOrDefaultKmH(mode.getMaximumSpeedKmH());
  }  

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends MacroscopicLinkSegmentType> getIdClass() {
    return MACROSCOPIC_LINK_SEGMENT_TYPE_ID_CLASS;
  }

  /** Remove mode access for the passed in modes
   * 
   * @param toBeRemovedModes all the modes to make i
   */
  public default void removeModeAccess(final Set<Mode> toBeRemovedModes) {
    toBeRemovedModes.forEach( mode -> removeModeAccess(mode));
  }

}
