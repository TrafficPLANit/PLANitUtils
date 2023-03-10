package org.goplanit.utils.service.routed;

import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface for wrapper container class around RoutedTrip instances that define an explicit schedule.
 * 
 * @author markr
 *
 */
public interface RoutedTripsSchedule extends RoutedTrips<RoutedTripSchedule> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripScheduleFactory getFactory();

  /**
   * Group the individual schedules by (unique combination of) leg segments
   *
   * @return grouped by result
   */
  public default Map<List<ServiceLegSegment>, List<RoutedTripSchedule>> groupByServiceLegSegments(){
    return groupBy(rts -> rts.getRelativeLegTimingsAsStream().map( rlt -> rlt.getParentLegSegment()).collect(Collectors.toList()));
  }

  /**
   * Group the individual schedules by (unique combination of) physical link segments
   *
   * @return grouped by result
   */
  public default Map<List<LinkSegment>, List<RoutedTripSchedule>> groupByPhysicalLinkSegments(){
    return groupBy(rts -> rts.getRelativeLegTimingsAsStream().flatMap( rlt -> rlt.getParentLegSegment().getPhysicalParentSegments().stream()).collect(Collectors.toList()));
  }

  /**
   * Group the individual schedules by (unique combination of) relative leg timings
   *
   * @return grouped by result
   */
  public default Map<List<RelativeLegTiming>, List<RoutedTripSchedule>> groupByRelativeLegTimings(){
    return groupBy(rts -> rts.getRelativeLegTimingsAsStream().collect(Collectors.toList()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsSchedule shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsSchedule deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsSchedule deepCloneWithMapping(BiConsumer<RoutedTripSchedule, RoutedTripSchedule> mapper);

}
