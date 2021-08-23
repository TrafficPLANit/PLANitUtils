package org.planit.utils.network.layer.macroscopic;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.planit.utils.id.ExternalIdAble;
import org.planit.utils.id.ManagedId;
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
   * Default capacity per lane (pcu/h)
   */
  public static final double DEFAULT_CAPACITY_LANE = 1800.0;
  
  /**
   * Default capacity per lane (pcu/h)
   */
  public static final double DEFAULT_MAX_DENSITY_LANE = 180.0;  
  
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
   * Return the capacity per lane of this macroscopic link segment type
   * 
   * @return capacity per lane in pcu/h/lane
   */
  public abstract double getCapacityPerLane();

  /**
   * Return the maximum density per lane for this macroscopic link segment type
   * 
   * @return the maximum density per lane in pcu/km/lane
   */
  public abstract double getMaximumDensityPerLane();

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
  public abstract boolean isModeAvailable(final Mode mode);   
  
  /**
   * return the available modes present in one of the access groups that have been registered
   * 
   * @return available modes
   */
  public abstract Set<Mode> getAvailableModes();
  
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

  /** Method which identifies which of the passed in modes is unavailable on the link segment
   * 
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are not available
   */
  public default Set<Mode> getUnAvailableModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> !isModeAvailable(mode)).collect(Collectors.toSet());
  }
  
  /** Method which identifies which of the passed in modes is available on the link segment
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are available
   */
  public default Set<Mode> getAvailableModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> isModeAvailable(mode)).collect(Collectors.toSet());
  }
  
  /** Method which identifies which of the passed in modes is available on the link segment but not in the passed in collection of modes
   * @param modes to exclude from the available modes
   * @return collection which is a subset of the available modes, namely excludes the the passed in modes
   */
  public default Set<Mode> getAvailableModesNotIn(final Collection<Mode> modes){
    return getAvailableModes().stream().filter(mode -> !modes.contains(mode)).collect(Collectors.toSet());
  }  
  
  /** Verify if the link segment type has any modes available
   * 
   * @return true when at least one mode is available
   */
  public default boolean hasAvailableModes() {
    return getAvailableModes()!=null && !getAvailableModes().isEmpty();
  }

  /** find group access properties that are equal to the ones that are passed in except for the allowed modes, i.e.,
   * find existing access properties for any mode that match the ones provided. IF found they are returned, otherwise null is returned
   * 
   * @param accessProperties to match against
   * @return access properties found matching, null if no match is found
   */
  public abstract AccessGroupProperties findEqualAccessPropertiesForAnyMode(AccessGroupProperties accessProperties);

}
