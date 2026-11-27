package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.Event;

/**
 * Event fired by the macroscopic network layer modifier about a change to toll roads
 * <p>
 * For example the removal of a toll road left without link segments, so a network pricing can take the road off the
 * schemes charging on it.
 * </p>
 *
 * @author markr
 */
public interface TollRoadModificationEvent extends Event {

}
