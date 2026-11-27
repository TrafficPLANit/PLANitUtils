package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoad;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Event fired when a toll road has been removed from its layer, as it was left without link segments
 * <p>
 * So a component referring to toll roads can follow, for example a network pricing taking the road off the schemes
 * charging on it.
 * </p>
 *
 * @author markr
 */
public class RemoveTollRoadEvent extends EventImpl implements TollRoadModificationEvent {

  /** event type fired off when a toll road has been removed */
  public static final TollRoadModifierEventType EVENT_TYPE =
      new TollRoadModifierEventType("TOLLROADMODIFIER.TOLLROAD.REMOVE");

  /**
   * Constructor
   *
   * @param source layer modifier firing the event
   * @param removedTollRoad the toll road removed
   */
  public RemoveTollRoadEvent(final MacroscopicNetworkLayerModifier source, final TollRoad removedTollRoad) {
    super(EVENT_TYPE, source, new Object[] {removedTollRoad});
  }

  /**
   * The removed toll road
   *
   * @return removed toll road
   */
  public TollRoad getRemovedTollRoad() {
    return (TollRoad) getContent()[0];
  }
}
