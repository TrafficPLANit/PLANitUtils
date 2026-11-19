package org.goplanit.utils.event;

/**
 * Which end of an operation an event reports
 * <p>
 * Some operations change several things in turn, and a listener may need to act both before anything has changed and
 * once everything has. Such an operation fires one event type twice, first with {@link #START} and then with
 * {@link #DONE}, rather than two differently named event types. The states are general, so every operation reported at
 * both ends uses the same ones. Most events report neither and have no state, see {@link Event#getState()}
 * </p>
 *
 * @author markr
 */
public enum EventState {

  /** The operation is about to start, nothing has changed yet */
  START,

  /** The operation is done, all its changes are made */
  DONE;
}
