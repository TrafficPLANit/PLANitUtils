package org.goplanit.utils.zoning.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.TransferZones;
import org.goplanit.utils.zoning.modifier.ZoningModifier;

/**
 * Event fired when the ids of a zoning's zones may have changed, carrying its OD zones and transfer zones
 * <p>
 * What indexes zones by id has to follow, such as the connectoids and the transfer zone groups of a zoning, which
 * listen for it. For example, removing zones and making the ids of those left contiguous again changes the id of every
 * zone after the first one removed.
 * </p>
 *
 * @author markr
 *
 */
public class ModifiedZoneIdsEvent extends EventImpl implements ZoningModificationEvent {

  /** event type fired off when zone ids have been modified */
  public static final ZoningModifierEventType EVENT_TYPE =
      new ZoningModifierEventType("ZONINGEVENT.MODIFIED_ZONE_IDS");

  /**
   * Wrapper for a modified zoning ids event indicating that one or more managed internal ids
   * of the zoning have been changed
   *
   * @param source zoning modifier firing the event
   * @param odZones of the zoning, whose ids may have changed
   * @param transferZones of the zoning, whose ids may have changed
   */
  public ModifiedZoneIdsEvent(ZoningModifier source, OdZones odZones, TransferZones transferZones) {
    super(EVENT_TYPE, source, new Object[] {odZones, transferZones});
  }

  /**
   * The OD zones of the zoning on which the modification took place
   *
   * @return the OD zones
   */
  public OdZones getOdZones() {
    return (OdZones) getContent()[0];
  }

  /**
   * The transfer zones of the zoning on which the modification took place
   *
   * @return the transfer zones
   */
  public TransferZones getTransferZones() {
    return (TransferZones) getContent()[1];
  }

}
