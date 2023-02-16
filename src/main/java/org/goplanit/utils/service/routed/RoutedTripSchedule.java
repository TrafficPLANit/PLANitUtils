package org.goplanit.utils.service.routed;

import java.time.LocalTime;
import java.util.List;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;

/**
 * The schedule with on or more departures for a routed service as well as the relative timings of each leg for each departure. Each leg timing is in an ordered position, meaning
 * that the first timing represents the first leg of the routed service and the last leg the final leg etc.
 * 
 * @author markr
 *
 */
public interface RoutedTripSchedule extends RoutedTrip, Iterable<RelativeLegTiming> {

  /**
   * Access to the departures of this schedule
   * 
   * @return departures
   */
  public abstract RoutedTripDepartures getDepartures();

  /**
   * Verify if any departures are present on the schedule
   *
   * @return true when present, false otherwise
   */
  public default boolean hasDepartures(){
    return !getDepartures().isEmpty();
  }

  /**
   * Clear all leg timings from the trip
   */
  public abstract void clearRelativeLegTimings();

  /**
   * Clear all departures from the trip
   */
  public abstract void clearDepartures();

  /**
   * Add a new leg's timing to the end of the already registered leg timings.
   * 
   * @param parentLegSegment (directed leg) to add to the trip's route
   * @param duration         duration of the leg segment
   * @param dwellTime        at the destination of the leg segment
   * @return the added timing
   */
  public abstract RelativeLegTiming addRelativeLegSegmentTiming(final ServiceLegSegment parentLegSegment, final LocalTime duration, final LocalTime dwellTime);

  /**
   * Collect a leg timing based on its index
   * 
   * @param index to collect
   * @return the relative leg timing found
   */
  public abstract RelativeLegTiming getRelativeLegTiming(int index);

  /**
   * Collect the number of registered leg timings
   * 
   * @return number of relative leg timings registered
   */
  public abstract int getRelativeLegTimingsSize();

  /**
   * Collect the last relative leg timing available, i.e., having the highest index
   *
   * @return found relative leg timing if any, otherwise null
   */
  public default RelativeLegTiming getLastRelativeLegTiming(){
    return getRelativeLegTiming(getLastRelativeLegTimingIndex());
  }

  /**
   * Get last index that is valid, when no entries exist, -1 is returned
   *
   * @return last valid index, -1 if no valid entries exist
   */
  public default int getLastRelativeLegTimingIndex(){
    return hasRelativeLegTimings() ? getRelativeLegTimingsSize()-1 : -1;
  }

  /**
   * Get default
   *
   * @return default dwell time
   */
  public abstract LocalTime getDefaultDwellTime();

  /**
   * Update the default dwell time to the most commonly found dwell time across all rell timings and
   * update the relative lge timings acccordingly
   *
   * @return updated default dwell time
   */
  public abstract LocalTime updateDefaultDwellTimeToMostCommon();

  /**
   * Collect the first relative leg timing available, i.e., having the lowest index
   *
   * @return found relative leg timing if any, otherwise null
   */
  public default RelativeLegTiming getFirstRelativeLegTiming(){
    return getRelativeLegTiming(0);
  }

  /**
   * Verify if any relative leg timing entries exist
   *
   * @return true when present, false otherwise
   */
  public default boolean hasRelativeLegTimings(){
    return getRelativeLegTimingsSize()>0;
  }

  /**
   * Verify if given index is a valid relative leg timings index
   *
   * @param relTimingsIndex to verify
   * @return true when entry exists, false otherwise
   */
  public default boolean isValidRelativeLegTimingsIndex(int relTimingsIndex){
    return relTimingsIndex >0 && relTimingsIndex < getRelativeLegTimingsSize();
  }

  /**
   * Clear the instance by removing both departures and leg timings from it. Use with caution
   */
  public default void clear(){
    clearDepartures();
    clearRelativeLegTimings();
  }

  /**
   * Remove the leg timing with the given index from the leg timings
   *
   * @param legTimingIndex to remove
   */
  public abstract void removeLegTiming(int legTimingIndex);

  /**
   * Remove the leg timings in ascending order. Note that after removal indices might have shifted due to removal
   * of entries
   *
   * @param legTimingIndices to remove, should be in ascending order
   */
  public default void removeLegTimingsIn(List<Integer> legTimingIndices){
    var iter = legTimingIndices.iterator();
    int indexOffset = 0;
    int prevIndex = -1;
    while(iter.hasNext()){
      int indexToRemove = iter.next() - indexOffset;
      if(indexToRemove <= prevIndex  || indexToRemove < 0){
        throw new PlanItRunTimeException(
                String.format("leg timing indices cannot contain duplicates and should be provided in valid ascending order (curr %d, prev %d"), indexToRemove, prevIndex);
      }
      removeLegTiming(indexToRemove);
      ++indexOffset; // each removed entry shifts remaining indices one to the left, account for that
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripSchedule shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripSchedule deepClone();

}
