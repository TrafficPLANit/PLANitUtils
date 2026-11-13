package org.goplanit.utils.containers;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * A map from long keys to values, keeping each value in an array at the position of its key counted from the first key
 * it holds, iterated in ascending key order as a TreeMap is
 * <p>
 * Why: PLANit's containers hold millions of entities by their id, and ids are numbered in sequence within their group,
 * so an id can be a position in an array. Counting from the first key keeps that true for ids continuing a sequence
 * shared with another container, such as transfer zones numbered after the OD zones. A TreeMap holds an entry and a
 * boxed key per value, about 64 bytes; a position holds 4. Where the keys do not suit an array, a key below the first
 * one, or one so far beyond the others that the array would take more memory than a TreeMap, every entry moves into a
 * TreeMap kept inside, so any long key works as in a TreeMap. Emptied, the map counts from the next key it holds.
 * </p>
 * <p>
 * Otherwise it behaves as a TreeMap of Long keys: a null value is held, a null key is refused and a key that is not a
 * Long fails its cast; an iterator removes through itself and stops with a {@link ConcurrentModificationException}
 * once the map gains or loses a key other than through it; a value set through an entry is set in the map.
 * </p>
 *
 * @param <V> type of value
 */
public class LongKeyArrayMap<V> extends AbstractMap<Long, V> {

  /** An entry handed out by an iterator, setting its value in the map while the map still holds the value handed out */
  private final class Entry extends AbstractMap.SimpleEntry<Long, V> {

    /** position of the entry's key in the array */
    private final int position;

    /**
     * Constructor
     *
     * @param position holding a value
     */
    private Entry(int position) {
      super(keyAt(position), LongKeyArrayMap.<V>unmask(valueByKey[position]));
      this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V setValue(V value) {
      if (entriesInTreeMap == null && positionOf(getKey()) == position && position < valueByKey.length
          && valueByKey[position] == mask(getValue())) {
        valueByKey[position] = mask(value);
      }
      return super.setValue(value);
    }
  }

  /**
   * Iterator over the positions holding a value, in ascending key order, removing through itself and stopping once
   * the map gains or loses a key other than through it
   *
   * @param <T> type handed out for a position
   */
  private abstract class PositionIterator<T> implements Iterator<T> {

    /** the array iterated, so a change that replaces the array is still seen as a change */
    private final Object[] positions = valueByKey;

    /** next position holding a value, the array's length when none */
    private int nextPosition = findPositionFrom(positions, 0);

    /** position last handed out, -1 when none or removed since */
    private int lastPosition = -1;

    /** number of changes of the map this iterator has seen */
    private int expectedChangeCount = changeCount;

    /**
     * What to hand out for a position
     *
     * @param position holding a value
     * @return what to hand out
     */
    protected abstract T handOut(int position);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
      return nextPosition < positions.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T next() {
      if (changeCount != expectedChangeCount) {
        throw new ConcurrentModificationException();
      }
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastPosition = nextPosition;
      nextPosition = findPositionFrom(positions, nextPosition + 1);
      return handOut(lastPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
      if (lastPosition < 0) {
        throw new IllegalStateException();
      }
      if (changeCount != expectedChangeCount) {
        throw new ConcurrentModificationException();
      }
      removeAt(lastPosition);
      lastPosition = -1;
      expectedChangeCount = changeCount;
    }
  }

  /** The entries of the map as a set, backed by the map */
  private final class EntrySetView extends AbstractSet<Map.Entry<Long, V>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Map.Entry<Long, V>> iterator() {
      if (entriesInTreeMap != null) {
        return entriesInTreeMap.entrySet().iterator();
      }
      return new PositionIterator<Map.Entry<Long, V>>() {
        @Override
        protected Map.Entry<Long, V> handOut(int position) {
          return new Entry(position);
        }
      };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
      return LongKeyArrayMap.this.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
      LongKeyArrayMap.this.clear();
    }
  }

  /** The values of the map as a collection in ascending key order, backed by the map */
  private final class ValuesView extends AbstractCollection<V> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<V> iterator() {
      if (entriesInTreeMap != null) {
        return entriesInTreeMap.values().iterator();
      }
      return new PositionIterator<V>() {
        @Override
        protected V handOut(int position) {
          return unmask(valueByKey[position]);
        }
      };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
      return LongKeyArrayMap.this.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
      LongKeyArrayMap.this.clear();
    }
  }

  /** stands for a null value in the array, where null marks a position without a key */
  private static final Object NULL_VALUE = new Object();

  /** number of positions of a new map */
  private static final int INITIAL_CAPACITY = 16;

  /**
   * most positions per value the array may grow to: a TreeMap takes about 64 bytes per entry and a position 4, so up
   * to this many positions per value the array takes no more memory than a TreeMap would
   */
  private static final int MAX_POSITIONS_PER_VALUE = 16;

  /** most positions an array can have */
  private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

  /**
   * The value as held in the array
   *
   * @param value to hold, may be null
   * @return what the array holds for it
   */
  private static Object mask(Object value) {
    return value == null ? NULL_VALUE : value;
  }

  /**
   * The value the array holds
   *
   * @param held by the array at a position with a key
   * @param <T> type of value
   * @return the value, null for a null value
   */
  @SuppressWarnings("unchecked")
  private static <T> T unmask(Object held) {
    return held == NULL_VALUE ? null : (T) held;
  }

  /**
   * The key as a long, refusing null and failing the cast for a key that is not a Long, as a TreeMap of Long keys does
   *
   * @param key to convert
   * @return the key's value
   */
  private static long toLong(Object key) {
    return ((Long) Objects.requireNonNull(key)).longValue();
  }

  /**
   * The first position at or after the given one holding a value
   *
   * @param positions array to search
   * @param position to start from
   * @return the position found, the array's length when none
   */
  private static int findPositionFrom(Object[] positions, int position) {
    int found = position;
    while (found < positions.length && positions[found] == null) {
      ++found;
    }
    return found;
  }

  /** each value at the position of its key, null where there is no key; empty once the entries are in the tree map */
  private Object[] valueByKey = new Object[INITIAL_CAPACITY];

  /** number of values in the array */
  private int size = 0;

  /** number of times the map gained or lost a key, so an iterator stops once that happens other than through it */
  private int changeCount = 0;

  /** the entries once a key did not suit the array, null until then */
  private TreeMap<Long, V> entriesInTreeMap = null;

  /** the key at the array's first position: the first key the map held while empty */
  private long keyAtFirstPosition = 0;

  /**
   * The position in the array of a key, counted from the key at the first position
   *
   * @param key to place
   * @return its position, -1 when the key is below the first position's or beyond the most positions an array can have
   */
  private int positionOf(long key) {
    if (key < keyAtFirstPosition) {
      return -1;
    }
    // exact when read as unsigned, as the key is not below the first position's
    long positionFromFirst = key - keyAtFirstPosition;
    return Long.compareUnsigned(positionFromFirst, MAX_ARRAY_LENGTH) < 0 ? (int) positionFromFirst : -1;
  }

  /**
   * The key of a position in the array
   *
   * @param position in the array
   * @return its key
   */
  private long keyAt(int position) {
    return keyAtFirstPosition + position;
  }

  /**
   * The most positions the array may have once it holds one more value, see {@link #MAX_POSITIONS_PER_VALUE}
   *
   * @return the most positions
   */
  private long findMaxLengthForOneMore() {
    return Math.min(MAX_ARRAY_LENGTH, (long) MAX_POSITIONS_PER_VALUE * (size + 1) + INITIAL_CAPACITY);
  }

  /**
   * Whether a key's position suits the array: within the array or within the most positions it may grow to
   *
   * @param position of the key, see {@link #positionOf(long)}
   * @return true when it suits the array
   */
  private boolean fitsArray(int position) {
    return position >= 0 && (position < valueByKey.length || position < findMaxLengthForOneMore());
  }

  /**
   * Remove the value at a position holding one
   *
   * @param position to clear
   */
  private void removeAt(int position) {
    valueByKey[position] = null;
    --size;
    ++changeCount;
  }

  /** Move every entry to the tree map, which holds them from then on */
  private void moveEntriesToTreeMap() {
    var treeMap = new TreeMap<Long, V>();
    for (int position = findPositionFrom(valueByKey, 0); position < valueByKey.length;
        position = findPositionFrom(valueByKey, position + 1)) {
      treeMap.put(keyAt(position), unmask(valueByKey[position]));
    }
    entriesInTreeMap = treeMap;
    valueByKey = new Object[0];
    size = 0;
    ++changeCount;
  }

  /**
   * Constructor of an empty map, also used by {@link org.goplanit.utils.wrapper.MapWrapperImpl} to make an empty map
   * of the same kind when copying
   */
  public LongKeyArrayMap() {
  }

  /**
   * Whether the entries are kept in the array, as they are until a key does not suit it
   *
   * @return true when in the array, false once in the tree map
   */
  public boolean keepsEntriesInArray() {
    return entriesInTreeMap == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return entriesInTreeMap != null ? entriesInTreeMap.size() : size;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(Object key) {
    long longKey = toLong(key);
    if (entriesInTreeMap != null) {
      return entriesInTreeMap.containsKey(longKey);
    }
    int position = positionOf(longKey);
    return position >= 0 && position < valueByKey.length && valueByKey[position] != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V get(Object key) {
    long longKey = toLong(key);
    if (entriesInTreeMap != null) {
      return entriesInTreeMap.get(longKey);
    }
    int position = positionOf(longKey);
    return position >= 0 && position < valueByKey.length ? unmask(valueByKey[position]) : null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V put(Long key, V value) {
    long longKey = toLong(key);
    if (entriesInTreeMap == null && size == 0) {
      keyAtFirstPosition = longKey;
    }
    int position = positionOf(longKey);
    if (entriesInTreeMap == null && !fitsArray(position)) {
      moveEntriesToTreeMap();
    }
    if (entriesInTreeMap != null) {
      return entriesInTreeMap.put(longKey, value);
    }

    if (position >= valueByKey.length) {
      long grownLength = Math.max(position + 1L, valueByKey.length + (valueByKey.length >> 1));
      valueByKey = Arrays.copyOf(valueByKey, (int) Math.min(findMaxLengthForOneMore(), grownLength));
    }
    var previous = valueByKey[position];
    valueByKey[position] = mask(value);
    if (previous == null) {
      ++size;
      ++changeCount;
    }
    return unmask(previous);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V remove(Object key) {
    long longKey = toLong(key);
    if (entriesInTreeMap != null) {
      return entriesInTreeMap.remove(longKey);
    }
    int position = positionOf(longKey);
    if (position < 0 || position >= valueByKey.length || valueByKey[position] == null) {
      return null;
    }
    var previous = valueByKey[position];
    removeAt(position);
    return unmask(previous);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Into an empty map, from another LongKeyArrayMap holding its entries in its array, the array is copied whole, so a
   * copy keeps the array however spread the keys left after removals are
   * </p>
   */
  @Override
  public void putAll(Map<? extends Long, ? extends V> other) {
    if (other instanceof LongKeyArrayMap && isEmpty() && entriesInTreeMap == null
        && ((LongKeyArrayMap<?>) other).entriesInTreeMap == null) {
      var otherArrayMap = (LongKeyArrayMap<?>) other;
      valueByKey = otherArrayMap.valueByKey.clone();
      size = otherArrayMap.size;
      keyAtFirstPosition = otherArrayMap.keyAtFirstPosition;
      ++changeCount;
      return;
    }
    super.putAll(other);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    if (entriesInTreeMap != null) {
      entriesInTreeMap.clear();
      entriesInTreeMap = null;
    }
    valueByKey = new Object[INITIAL_CAPACITY];
    size = 0;
    keyAtFirstPosition = 0;
    ++changeCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void forEach(BiConsumer<? super Long, ? super V> action) {
    Objects.requireNonNull(action);
    if (entriesInTreeMap != null) {
      entriesInTreeMap.forEach(action);
      return;
    }
    var positions = valueByKey;
    int expectedChangeCount = changeCount;
    for (int position = findPositionFrom(positions, 0); position < positions.length;
        position = findPositionFrom(positions, position + 1)) {
      V value = unmask(positions[position]);
      action.accept(keyAt(position), value);
      if (changeCount != expectedChangeCount) {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Map.Entry<Long, V>> entrySet() {
    return new EntrySetView();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<V> values() {
    return new ValuesView();
  }
}
