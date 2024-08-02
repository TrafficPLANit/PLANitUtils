package org.goplanit.utils.network.layer.macroscopic;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.misc.CollectionUtils;
import org.goplanit.utils.mode.Mode;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A container interface for macroscopic link segment types
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypes extends ManagedIdEntities<MacroscopicLinkSegmentType> {
   
  /**
   * Return a MacroscopicLinkSegmentType by its Xml id
   * 
   * @param xmlId the XML id of the MacroscopicLinkSegmentType
   * @return the specified MacroscopicLinkSegmentType instance
   */
  public abstract MacroscopicLinkSegmentType getByXmlId(String xmlId);  
         
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypeFactory getFactory();


  /**
   * Convenience method to determine the maximum speed limit across all types for a given mode
   *
   * @param mode to use
   * @return found maximum applied speed limit
   */
  public default double findMaximumSpeedLimit(Mode mode){
    double maxSpeedLimitKmH = Double.NEGATIVE_INFINITY;
    for(var linkSegmentType : this) {
      if(linkSegmentType.isModeAllowed(mode)){
        double maxSpeedCurr = linkSegmentType.getMaximumSpeedKmH(mode);
        if(maxSpeedCurr > maxSpeedLimitKmH ){
          maxSpeedLimitKmH = maxSpeedCurr;
        }
      }
    }
    return maxSpeedLimitKmH;
  }

  /**
   * Construct a list of entries that are functionallu identical (if any), so excluding name, and id fields
   * but considering all other fields with meaning
   *
   * @return list of functional duplicates, empty if none found
   */
  public default List<SortedSet<MacroscopicLinkSegmentType>> findFunctionalDuplicates(){
    List<SortedSet<MacroscopicLinkSegmentType>> duplicates = new ArrayList<>(2);
    Set<MacroscopicLinkSegmentType> skipSet = new HashSet<>();
    for(var ls1 : this) {
      skipSet.add(ls1);
      SortedSet<MacroscopicLinkSegmentType> duplicatesForLs1 = null;
      for(var ls2 : this) {
        if(skipSet.contains(ls2)) continue;

        boolean isEqual = Objects.equals(ls1.getExplicitCapacityPerLaneOrDefault(), ls2.getExplicitCapacityPerLaneOrDefault()) &&
          Objects.equals(ls1.getExplicitMaximumDensityPerLaneOrDefault(), ls2.getExplicitMaximumDensityPerLaneOrDefault()) &&
          Objects.equals(ls1.getAllowedModes(), ls2.getAllowedModes()) &&
          ls1.getAllowedModes().stream().allMatch( m -> Objects.equals(ls1.getAccessProperties(m), ls2.getAccessProperties(m)));
        if(isEqual){
          duplicatesForLs1 = duplicates.stream().filter(e -> e.contains(ls1)).findFirst().orElseGet(TreeSet::new);
          if(duplicatesForLs1.isEmpty()){
            duplicates.add(duplicatesForLs1);
          }
          duplicatesForLs1.add(ls1);
          duplicatesForLs1.add(ls2);
        }
      }
    }
    return duplicates;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentTypes deepCloneWithMapping(
          BiConsumer<MacroscopicLinkSegmentType, MacroscopicLinkSegmentType> mapper);

}
