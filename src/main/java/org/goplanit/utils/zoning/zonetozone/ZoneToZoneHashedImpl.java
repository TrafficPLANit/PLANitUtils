package org.goplanit.utils.zoning.zonetozone;

import java.util.HashMap;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * This class stores paths by their origin and destination by creating a unique hash for the combined ids of the zones.
 * This results in a memory efficient implementation requiring only a single hash based container,
 * instead of having as many containers as there are origins. It also means only conducting a single lookup despite
 * the fact we have two keys (from and to).
 *
 * @author markr
 *
 */
public class ZoneToZoneHashedImpl<T> extends ZoneToZoneDataImpl<T> implements ZoneToZoneHashed<T> {

  private static final Logger LOGGER = Logger.getLogger(ZoneToZoneHashedImpl.class.getCanonicalName());

  /** the hashed OD values where the key is based on the combined ids of origin and destination */
  protected final HashMap<Integer, T> odHashed;

  /**
   * Constructor
   *
   * @param idClass concrete class to use for id generation
   * @param groupId contiguous id generation within this group for instances of this class
   * @param valueClass class of the values in container
   * @param zones   the zones being used
   */
  public ZoneToZoneHashedImpl(
          Class<? extends IdAble> idClass,
          final IdGroupingToken groupId,
          Class<T> valueClass,
          final Zones<? extends Zone> zones) {
    super(idClass, groupId, valueClass, zones);
    this.odHashed = new HashMap<>();
  }

  /**
   * Constructor, using OdHashedImpl as the class type
   *
   * @param groupId contiguous id generation within this group for instances of this class
   * @param valueClass class of the values in container
   * @param zones   the zones being used
   */
  public ZoneToZoneHashedImpl(
      final IdGroupingToken groupId,
      Class<T> valueClass,
      final Zones<? extends Zone> zones) {
    this(ZoneToZoneHashedImpl.class, groupId, valueClass, zones);
  }

  /**
   * Copy constructor, only shallow copy implemented, deep copy is to be implemented by parent because we do not
   * want to enforce more type information on values
   * 
   * @param other to copy from
   */
  public ZoneToZoneHashedImpl(final ZoneToZoneHashedImpl<? extends T> other) {
    super(other);
    this.odHashed = new HashMap<>(other.odHashed);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(Zone from, Zone to) {
    /* hash to single key */
    return odHashed.get(ZoneToZoneHashed.generateHashKey(from.getId(), to.getId()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long from, long to) {
    /* hash to single key */
    return odHashed.get(ZoneToZoneHashed.generateHashKey(from, to));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Zone from, Zone to, T value) {
    odHashed.put(ZoneToZoneHashed.generateHashKey(from.getId(), to.getId()), value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @NonNull ZoneToZoneHashedIterator<T> iterator(){
    return new ZoneToZoneHashedIterator<>(this, getZones());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ZoneToZoneHashedImpl<T> shallowClone(){
    return new ZoneToZoneHashedImpl<>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ZoneToZoneHashedImpl<T> deepClone(){
    LOGGER.severe("No deep copy available of OdHashedImpl instance due to unknown type T, shallow copy instead");
    return shallowClone();
  }

  // getters - setters

}
