package org.goplanit.utils.od;

import java.util.Arrays;
import java.util.HashMap;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;

/**
 * This class stores paths by their origin and destination by creating a unique hash for the combined ids of the od zones. This results in a memory efficient implementation
 * requiring only a single hash based container, instead of having as many containers as their are origins. It also means only conducting a single lookup despite the fact we have
 * two keys (o and d).
 *
 * @author markr
 *
 */
public abstract class OdHashedImpl<T> extends OdDataImpl<T> implements OdHashed<T> {

  /** the hashed OD values where the key is based on the combined ids of origin and destination */
  protected final HashMap<Integer, T> odHashed;

  /**
   * Constructor
   *
   * @param idClass concrete class to use for id generation
   * @param groupId contiguous id generation within this group for instances of this class
   * @param zones   the zones being used
   */
  public OdHashedImpl(Class<? extends IdAble> idClass, final IdGroupingToken groupId, final OdZones zones) {
    super(idClass, groupId, zones);
    this.odHashed = new HashMap<Integer, T>();
  }

  /**
   * Copy constructor, only shallow copy implemented, deep copy is to be implemented by parent because we do not
   * want to enforce more type information on values
   * 
   * @param other to copy from
   */
  public OdHashedImpl(final OdHashedImpl<T> other) {
    super(other);
    this.odHashed = new HashMap<>(other.odHashed);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(Zone origin, Zone destination) {
    /* hash to single key */
    return odHashed.get(Arrays.hashCode(new long[] { origin.getId(), destination.getId() }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long originId, long destinationId) {
    /* hash to single key */
    return odHashed.get(Arrays.hashCode(new long[] { originId, destinationId }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Zone origin, Zone destination, T path) {
    odHashed.put(Arrays.hashCode(new long[] { origin.getId(), destination.getId() }), path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdHashedIterator<T> iterator();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdHashedImpl<T> clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdHashedImpl<T> deepClone();

  // getters - setters

}
