package org.goplanit.utils.service.routed;

import org.goplanit.utils.network.layer.service.ServiceLegSegment;

import java.util.stream.Stream;

/**
 * Interface to reflect one or more similar routed service trips by providing information on their route legs and schedule/frequencies.
 * 
 * @author markr
 *
 */
public interface RoutedServiceTripInfo {

  /**
   * {@inheritDoc}
   */
  public abstract RoutedServiceTripInfo shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract RoutedServiceTripInfo deepClone();

  /**
   * Trips for a service can be frequency or schedule based, or have both. Via this method we collect only the frequency based trips
   * 
   * @return frequency based trips of the service
   */
  public abstract RoutedTripsFrequency getFrequencyBasedTrips();

  /**
   * Trips for a service can be frequency or schedule based, or have both. Via this method we collect only the schedule based trips
   * 
   * @return schedule based trips of the service
   */
  public abstract RoutedTripsSchedule getScheduleBasedTrips();

  /**
   * Verify if any frequency based trips exists
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasFrequencyBasedTrips() {
    return getFrequencyBasedTrips() != null && !getFrequencyBasedTrips().isEmpty();
  }

  /**
   * Verify if any schedule based trips exists
   * 
   * @return True when present, false otherwise
   */
  public default boolean hasScheduleBasedTrips() {
    return getScheduleBasedTrips() != null && !getScheduleBasedTrips().isEmpty();
  }

  /**
   * Remove all registered frequency and schedule based trips
   */
  public abstract void reset();

  /**
   * Verify if any trips exist as either scheduled or frequency based
   *
   * @return true when present, false otherwise
   */
  public default boolean hasAnyTrips(){
    return hasScheduleBasedTrips() || hasFrequencyBasedTrips();
  }

  /**
   * Provide a stream of all service leg segments touched by this routed service
   *
   * @return service leg segments touched by this routed service
   */
  public default Stream<ServiceLegSegment> getLegSegmentsStream(){
    var scheduleStream = getScheduleBasedTrips().stream().flatMap(ts -> ts.getRelativeLegTimingsAsStream()).map(rlt -> rlt.getParentLegSegment());
    var freqStream = getFrequencyBasedTrips().stream().flatMap(ft -> ft.getLegSegmentsAsStream());
    return Stream.concat(scheduleStream,freqStream);
  }
}
