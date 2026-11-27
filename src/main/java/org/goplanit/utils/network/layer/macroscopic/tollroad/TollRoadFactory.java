package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

import java.util.List;

/**
 * Registers toll roads on the toll roads of a layer
 * <p>
 * The one way to add a toll road, so the rule that a link segment is on at most one toll road of its layer is kept in
 * one place: the network reader registers each {@code <tollroad>} through it, and later the OSM reader each toll road
 * it finds.
 * </p>
 *
 * @author markr
 */
public interface TollRoadFactory extends ManagedIdEntityFactory<TollRoad> {

  /**
   * Create a toll road and register it on its container. A link segment already on another toll road of the layer is
   * not added, with a warning saying why, as a link segment is on at most one toll road; one given twice is added once.
   * A named priced mode that none of its link segments allows is kept, with a warning, as priced modes say who pays,
   * not who may use the road
   *
   * @param linkSegments the toll road's link segments, of the container's layer
   * @param pricedModes the modes that pay on it, as given
   * @return the registered toll road, null when none of its link segments could be added, with a warning
   */
  public abstract TollRoad registerNew(List<MacroscopicLinkSegment> linkSegments, PricedModeSelection pricedModes);
}
