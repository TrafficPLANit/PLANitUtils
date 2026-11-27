package org.goplanit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.goplanit.utils.containers.EntityIndex;
import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdAbleImpl;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.id.ManagedIdEntitiesWithForeignKeyIndicesImpl;
import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests a container keeping an index of its entities by the keys they refer to current through every change made
 * through it: register, replace by id, remove by entity, by id and by condition, clear, copy, change through the
 * container, and reindexing after direct changes
 *
 * @author markr
 */
public class ManagedIdEntitiesWithForeignKeyIndicesTest {

  /** an entity with an id, referring to keys that can be changed */
  private static final class Referrer extends IdAbleImpl implements ManagedId {

    /** the keys referred to */
    private final List<Object> keys = new ArrayList<>();

    /**
     * Constructor
     *
     * @param id of the entity
     * @param keys referred to
     */
    private Referrer(long id, Object... keys) {
      super(id);
      this.keys.addAll(Arrays.asList(keys));
    }

    /**
     * Copy constructor
     *
     * @param other to copy
     */
    private Referrer(Referrer other) {
      super(other);
      this.keys.addAll(other.keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long recreateManagedIds(IdGroupingToken tokenId) {
      return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends IdAble> getIdClass() {
      return Referrer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Referrer shallowClone() {
      return new Referrer(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Referrer deepClone() {
      return new Referrer(this);
    }
  }

  /** container of referrers with an index by the keys they refer to */
  private static final class Referrers extends ManagedIdEntitiesWithForeignKeyIndicesImpl<Referrer> {

    /** the referrers by the keys they refer to */
    private final EntityIndex<Object, Referrer> byKey = addForeignKeyIndex(referrer -> referrer.keys);

    /**
     * Constructor
     */
    private Referrers() {
      super(Referrer::getId, Referrer.class);
    }

    /**
     * Copy constructor
     *
     * @param other to copy
     * @param deepCopy when true, create a deep copy, shallow copy otherwise
     */
    private Referrers(Referrers other, boolean deepCopy) {
      super(other, deepCopy, null);
    }

    /**
     * The referrers referring to a key
     *
     * @param key to look up
     * @return referrers in the order indexed
     */
    private List<Referrer> getByKey(Object key) {
      return byKey.get(key);
    }

    /**
     * Not used by these tests
     *
     * @return null
     */
    @Override
    public ManagedIdEntityFactory<Referrer> getFactory() {
      return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Referrers shallowClone() {
      return new Referrers(this, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Referrers deepClone() {
      return new Referrers(this, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Referrers deepCloneWithMapping(BiConsumer<Referrer, Referrer> mapper) {
      return new Referrers(this, true);
    }
  }

  /** the container under test */
  private Referrers referrers;

  /** first key */
  private Object node1;

  /** second key */
  private Object node2;

  /** Fresh container and keys */
  @BeforeEach
  public void setUp() {
    referrers = new Referrers();
    node1 = new Object();
    node2 = new Object();
  }

  /** A registered entity is found by what it refers to */
  @Test
  public void registeredEntityFoundByWhatItRefersTo() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    assertEquals(List.of(referrer), referrers.getByKey(node1));
  }

  /** An entity removed by entity or by id is no longer found */
  @Test
  public void removedEntityNoLongerFound() {
    var byEntity = new Referrer(0, node1);
    var byId = new Referrer(1, node2);
    referrers.register(byEntity);
    referrers.register(byId);
    referrers.remove(byEntity);
    referrers.remove(1L);
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertTrue(referrers.getByKey(node2).isEmpty());
  }

  /** Removing by a condition updates the index */
  @Test
  public void removingByConditionUpdatesIndex() {
    var atNode1 = new Referrer(0, node1);
    var atNode2 = new Referrer(1, node2);
    referrers.register(atNode1);
    referrers.register(atNode2);
    referrers.removeIf(referrer -> referrer.keys.contains(node1));
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertEquals(List.of(atNode2), referrers.getByKey(node2));
  }

  /** Registering another entity with the same id replaces the first in the index */
  @Test
  public void replacingByIdUpdatesIndex() {
    var first = new Referrer(0, node1);
    var second = new Referrer(0, node2);
    referrers.register(first);
    referrers.register(second);
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertEquals(List.of(second), referrers.getByKey(node2));
  }

  /** Clearing the container empties the index */
  @Test
  public void clearingEmptiesIndex() {
    referrers.register(new Referrer(0, node1));
    referrers.clear();
    assertTrue(referrers.getByKey(node1).isEmpty());
  }

  /** A shallow copy finds the entities it holds */
  @Test
  public void shallowCopyFindsItsOwnEntities() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    var copy = referrers.shallowClone();
    assertEquals(List.of(referrer), copy.getByKey(node1));
  }

  /** A deep copy finds its own copies of the entities, not the originals */
  @Test
  public void deepCopyFindsItsOwnEntities() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    var copy = referrers.deepClone();
    assertEquals(List.of(copy.get(0)), copy.getByKey(node1));
    assertNotSame(referrer, copy.get(0));
  }

  /** A change made through the container moves the entity in the index */
  @Test
  public void changeThroughContainerMovesEntityInIndex() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    referrers.update(referrer, changed -> changed.keys.add(node2));
    assertEquals(List.of(referrer), referrers.getByKey(node1));
    assertEquals(List.of(referrer), referrers.getByKey(node2));
  }

  /** Reindexing one entity after a direct change files it under its new key only */
  @Test
  public void reindexingOneEntityAfterDirectChange() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    referrer.keys.set(0, node2);
    referrers.reindex(referrer);
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertEquals(List.of(referrer), referrers.getByKey(node2));
  }

  /** Reindexing an entity the container does not hold leaves the index as it is */
  @Test
  public void reindexingUnregisteredEntityDoesNothing() {
    referrers.reindex(new Referrer(0, node1));
    assertTrue(referrers.getByKey(node1).isEmpty());
  }

  /** Reindexing every entity after direct changes files each under its new keys */
  @Test
  public void reindexingEveryEntityAfterDirectChanges() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    referrer.keys.set(0, node2);
    referrers.reindex();
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertEquals(List.of(referrer), referrers.getByKey(node2));
  }

  /** Removing an entity changed directly leaves it under neither its old nor its new key */
  @Test
  public void removingEntityChangedDirectlyLeavesNothing() {
    var referrer = new Referrer(0, node1);
    referrers.register(referrer);
    referrer.keys.set(0, node2);
    referrers.remove(referrer);
    assertTrue(referrers.getByKey(node1).isEmpty());
    assertTrue(referrers.getByKey(node2).isEmpty());
  }
}
