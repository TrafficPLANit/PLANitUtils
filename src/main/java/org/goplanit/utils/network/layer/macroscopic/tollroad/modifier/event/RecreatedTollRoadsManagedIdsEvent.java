package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoads;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Event fired when the ids of the toll roads of a layer have been recreated
 * <p>
 * So their XML ids can follow, as the XML ids of the other entities of the layer do when a network is renumbered
 * before it is written, see {@code SyncXmlIdToIdTollRoadsHandler}.
 * </p>
 *
 * @author markr
 */
public class RecreatedTollRoadsManagedIdsEvent extends EventImpl implements TollRoadModificationEvent {

  /** event type fired off when the ids of the toll roads have been recreated */
  public static final TollRoadModifierEventType EVENT_TYPE =
      new TollRoadModifierEventType("TOLLROADMODIFIER.MANAGEDIDENTITIES.RECREATED");

  /**
   * Constructor
   *
   * @param source layer modifier firing the event
   * @param tollRoads whose ids have been recreated
   */
  public RecreatedTollRoadsManagedIdsEvent(final MacroscopicNetworkLayerModifier source, final TollRoads tollRoads) {
    super(EVENT_TYPE, source, new Object[] {tollRoads});
  }

  /**
   * The toll roads whose ids have been recreated
   *
   * @return toll roads
   */
  public TollRoads getTollRoads() {
    return (TollRoads) getContent()[0];
  }
}
