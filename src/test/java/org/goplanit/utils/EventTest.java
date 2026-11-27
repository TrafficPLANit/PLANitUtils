package org.goplanit.utils;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.event.EventListener;
import org.goplanit.utils.event.EventState;
import org.goplanit.utils.event.EventType;
import org.goplanit.utils.event.TypedEventProducer;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventType;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.goplanit.utils.graph.modifier.event.RemoveEdgeSegmentEvent;
import org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event.RemoveTollRoadEvent;
import org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event.TollRoadModifierEventProducer;
import org.goplanit.utils.pricing.modifier.event.NetworkPricingModifierEventProducer;
import org.goplanit.utils.pricing.modifier.event.RemovePricingSchemeEvent;
import org.goplanit.utils.zoning.modifier.event.RecreatedZoningEntitiesManagedIdsEvent;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests on events: the state an event may report, saying which end of an operation it was fired at, and the producer
 * of one kind of event, of which an owner producing several kinds holds one per kind.
 */
public class EventTest {

  /** A listener to the events of kind A */
  private interface KindAListener extends EventListener {

    /**
     * Hear an event of kind A
     *
     * @param event heard
     */
    public void onKindAEvent(KindAEvent event);
  }

  /** A listener to the events of kind B */
  private interface KindBListener extends EventListener {

    /**
     * Hear an event of kind B
     *
     * @param event heard
     */
    public void onKindBEvent(KindBEvent event);
  }

  /** An event of kind A, of one of its two types */
  private static final class KindAEvent extends EventImpl {

    /**
     * Constructor
     *
     * @param type of the event, one of kind A's two
     * @param source of the event
     */
    public KindAEvent(final EventType type, final Object source) {
      super(type, source, new Object[] {});
    }
  }

  /** An event of kind B */
  private static final class KindBEvent extends EventImpl {

    /**
     * Constructor
     *
     * @param source of the event
     */
    public KindBEvent(final Object source) {
      super(KIND_B_TYPE, source, new Object[] {});
    }
  }

  /** A listener of both kinds, recording each event it hears as its name and the event's type, in order */
  private static final class RecordingListener implements KindAListener, KindBListener {

    /** the name it records events under */
    private final String name;

    /** where it records the events heard, shared by the listeners of a test so their order shows */
    private final List<String> heard;

    /**
     * Constructor
     *
     * @param name to record events under
     * @param heard where to record the events heard
     */
    public RecordingListener(String name, List<String> heard) {
      this.name = name;
      this.heard = heard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType[] getKnownSupportedEventTypes() {
      return new EventType[] {KIND_A_FIRST_TYPE, KIND_A_SECOND_TYPE, KIND_B_TYPE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onKindAEvent(KindAEvent event) {
      heard.add(name + " " + event.getType().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onKindBEvent(KindBEvent event) {
      heard.add(name + " " + event.getType().getName());
    }
  }

  /** A listener of kind A supporting the types it is given, which need not be of kind A */
  private static final class KindAListenerOfTypes implements KindAListener {

    /** the types it supports */
    private final EventType[] supportedTypes;

    /**
     * Constructor
     *
     * @param supportedTypes the types it supports
     */
    public KindAListenerOfTypes(EventType... supportedTypes) {
      this.supportedTypes = supportedTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType[] getKnownSupportedEventTypes() {
      return supportedTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onKindAEvent(KindAEvent event) {
    }
  }

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

  /** The first event type of kind A */
  private static final EventType KIND_A_FIRST_TYPE = new EventType("EVENTTEST.A_FIRST");

  /** The second event type of kind A */
  private static final EventType KIND_A_SECOND_TYPE = new EventType("EVENTTEST.A_SECOND");

  /** The event type of kind B */
  private static final EventType KIND_B_TYPE = new EventType("EVENTTEST.B");

  /**
   * A producer of kind A events
   *
   * @return the producer
   */
  private static TypedEventProducer<KindAListener, KindAEvent, EventType> createKindAProducer() {
    return new TypedEventProducer<>(KindAListener::onKindAEvent, Set.of(KIND_A_FIRST_TYPE, KIND_A_SECOND_TYPE));
  }

  /**
   * A producer of kind B events
   *
   * @return the producer
   */
  private static TypedEventProducer<KindBListener, KindBEvent, EventType> createKindBProducer() {
    return new TypedEventProducer<>(KindBListener::onKindBEvent, Set.of(KIND_B_TYPE));
  }

  /**
   * A listener of both kinds, added to the producers of both, hears each event once, as one producer for both kinds
   * would register it twice
   */
  @Test
  public void listenerOfTwoKindsHearsEachEventOnce() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    var kindB = createKindBProducer();
    var listener = new RecordingListener("listener", heard);
    kindA.addListener(listener);
    kindB.addListener(listener);

    kindA.fireEvent(new KindAEvent(KIND_A_FIRST_TYPE, this));
    kindB.fireEvent(new KindBEvent(this));
    assertEquals(List.of("listener EVENTTEST.A_FIRST", "listener EVENTTEST.B"), heard);
  }

  /** A listener of both kinds removed from the producer of one kind stays on the other */
  @Test
  public void listenerRemovedFromOneKindStaysOnTheOther() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    var kindB = createKindBProducer();
    var listener = new RecordingListener("listener", heard);
    kindA.addListener(listener);
    kindB.addListener(listener);

    kindA.removeListener(listener);
    kindA.fireEvent(new KindAEvent(KIND_A_FIRST_TYPE, this));
    kindB.fireEvent(new KindBEvent(this));
    assertEquals(List.of("listener EVENTTEST.B"), heard);
    assertFalse(kindA.hasListener(KIND_A_FIRST_TYPE));
    assertTrue(kindB.hasListener(KIND_B_TYPE));
  }

  /** An internal listener hears an event before the listeners added from outside, whatever order they were added in */
  @Test
  public void internalListenerHearsAnEventFirst() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    kindA.addListener(new RecordingListener("external", heard));
    kindA.addInternalListener(new RecordingListener("internal", heard));

    kindA.fireEvent(new KindAEvent(KIND_A_FIRST_TYPE, this));
    assertEquals(List.of("internal EVENTTEST.A_FIRST", "external EVENTTEST.A_FIRST"), heard);
  }

  /** A listener added for one event type of a kind hears no other type of that kind */
  @Test
  public void listenerAddedForOneTypeHearsNoOther() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    kindA.addListener(new RecordingListener("listener", heard), KIND_A_FIRST_TYPE);

    kindA.fireEvent(new KindAEvent(KIND_A_SECOND_TYPE, this));
    assertTrue(heard.isEmpty());
    kindA.fireEvent(new KindAEvent(KIND_A_FIRST_TYPE, this));
    assertEquals(List.of("listener EVENTTEST.A_FIRST"), heard);
  }

  /** A producer gives the types it was given to fire, and no other */
  @Test
  public void producerGivesTheTypesItFires() {
    assertEquals(Set.of(KIND_A_FIRST_TYPE, KIND_A_SECOND_TYPE), createKindAProducer().getFiredEventTypes());
    assertEquals(Set.of(KIND_B_TYPE), createKindBProducer().getFiredEventTypes());
  }

  /**
   * Firing an event of a type the producer does not list fails, and no listener hears it ("Event of a type not
   * listed")
   */
  @Test
  public void eventOfATypeNotListed() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    kindA.addListener(new RecordingListener("listener", heard));

    assertThrows(PlanItRunTimeException.class, () -> kindA.fireEvent(new KindAEvent(KIND_B_TYPE, this)));
    assertTrue(heard.isEmpty());
  }

  /**
   * A listener supporting no type the producer fires is refused, for every supported type and as an internal listener
   * ("Listener of types never fired")
   */
  @Test
  public void listenerOfTypesNeverFired() {
    var kindA = createKindAProducer();
    var listener = new KindAListenerOfTypes(KIND_B_TYPE);

    assertThrows(IllegalArgumentException.class, () -> kindA.addListener(listener));
    assertThrows(IllegalArgumentException.class, () -> kindA.addInternalListener(listener));
    assertFalse(kindA.hasListener(KIND_A_FIRST_TYPE));
  }

  /** Registering for one type the producer does not fire is refused ("Registering for a type never fired") */
  @Test
  public void registeringForATypeNeverFired() {
    var kindA = createKindAProducer();

    assertThrows(IllegalArgumentException.class,
        () -> kindA.addListener(new RecordingListener("listener", new ArrayList<>()), KIND_B_TYPE));
  }

  /**
   * A listener of both kinds, registered on the kind A producer for every type it supports, is registered for kind A's
   * types only and hears kind A's events
   */
  @Test
  public void listenerOfBothKindsGetsTheProducersShare() {
    var heard = new ArrayList<String>();
    var kindA = createKindAProducer();
    kindA.addListener(new RecordingListener("listener", heard));

    assertTrue(kindA.hasListener(KIND_A_FIRST_TYPE));
    assertTrue(kindA.hasListener(KIND_A_SECOND_TYPE));
    assertFalse(kindA.hasListener(KIND_B_TYPE));
    kindA.fireEvent(new KindAEvent(KIND_A_SECOND_TYPE, this));
    assertEquals(List.of("listener EVENTTEST.A_SECOND"), heard);
  }

  /**
   * Each producer interface's list holds event types of its own kind only: the graph list no directed graph type, a
   * directed graph type being a graph type too, and the zoning list the recreation of the zoning's entity ids ("Event
   * types of their producer's kind")
   */
  @Test
  public void eventTypesOfTheirProducersKind() {
    assertTrue(GraphModifierEventProducer.FIRED_GRAPH_EVENT_TYPES.stream()
        .noneMatch(type -> type instanceof DirectedGraphModifierEventType));
    assertTrue(DirectedGraphModifierEventProducer.FIRED_DIRECTED_GRAPH_EVENT_TYPES
        .contains(RemoveEdgeSegmentEvent.EVENT_TYPE));
    assertTrue(ZoningModifierEventProducer.FIRED_ZONING_EVENT_TYPES
        .contains(RecreatedZoningEntitiesManagedIdsEvent.EVENT_TYPE));
    assertTrue(TollRoadModifierEventProducer.FIRED_TOLL_ROAD_EVENT_TYPES.contains(RemoveTollRoadEvent.EVENT_TYPE));
    assertTrue(NetworkPricingModifierEventProducer.FIRED_NETWORK_PRICING_EVENT_TYPES
        .contains(RemovePricingSchemeEvent.EVENT_TYPE));
  }

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
