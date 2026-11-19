package org.goplanit.utils;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.event.EventState;
import org.goplanit.utils.event.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests on events: the state an event may report, saying which end of an operation it was fired at.
 */
public class EventTest {

  /** An event of the test event type, carrying no content of note */
  private static final class TestEvent extends EventImpl {

    /**
     * Constructor of an event without a state
     *
     * @param source of the event
     */
    public TestEvent(final Object source) {
      super(TEST_EVENT_TYPE, source, new Object[] {});
    }

    /**
     * Constructor of an event reporting one end of an operation
     *
     * @param source of the event
     * @param state the end of the operation the event reports
     */
    public TestEvent(final Object source, final EventState state) {
      super(TEST_EVENT_TYPE, source, new Object[] {}, state);
    }
  }

  /** Event type of the test events, registered once since event types are unique */
  private static final EventType TEST_EVENT_TYPE = new EventType("EVENTTEST.TEST");

  /**
   * An event made without a state reports none.
   */
  @Test
  public void eventWithoutAStateReportsNone() {
    assertTrue(new TestEvent(this).getState().isEmpty());
  }

  /**
   * An event made with a state reports that state, for both ends of an operation.
   */
  @Test
  public void eventWithAStateReportsIt() {
    assertEquals(EventState.START, new TestEvent(this, EventState.START).getState().orElseThrow());
    assertEquals(EventState.DONE, new TestEvent(this, EventState.DONE).getState().orElseThrow());
  }
}
