package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

import java.util.List;

/**
 * Registers toll roads on the toll roads of a layer
 * <p>
 * It makes and registers what it is given. A caller checks first, through the toll roads: a link segment is on at most
 * one toll road of its layer, so a new toll road takes only those {@link TollRoads#findLinkSegmentsNotOnATollRoad}
 * gives. The network reader does so for each {@code <tollroad>}, as does any other caller building toll roads.
 * </p>
 *
 * @author markr
 */
public interface TollRoadFactory extends ManagedIdEntityFactory<TollRoad> {

  /**
   * Create a toll road and register it on its container
   *
   * @param linkSegments the toll road's link segments, of the container's layer, each once and on no other toll road
   * @param pricedModes the modes that pay on it, as given
   * @return the registered toll road
   */
  public abstract TollRoad registerNew(List<MacroscopicLinkSegment> linkSegments, PricedModeSelection pricedModes);
}
