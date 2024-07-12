package org.goplanit.utils.od;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;

/**
 * This class stores paths by their origin and destination by creating a unique hash for the combined ids of the od zones.
 * This results in a memory efficient implementation requiring only a single hash based container,
 * instead of having as many containers as there are origins. It also means only conducting a single lookup despite the fact we have
 * two keys (o and d).
 *
 * @author markr
 *
 */
public class OdHashedImpl<T> extends OdDataImpl<T> implements OdHashed<T> {

  private static final Logger LOGGER = Logger.getLogger(OdHashedImpl.class.getCanonicalName());

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
  public OdHashedImpl(
          Class<? extends IdAble> idClass, final IdGroupingToken groupId, Class<T> valueClass, final OdZones zones) {
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
  public OdHashedImpl(final IdGroupingToken groupId, Class<T> valueClass, final OdZones zones) {
    this(OdHashedImpl.class, groupId, valueClass, zones);
  }

  /**
   * Copy constructor, only shallow copy implemented, deep copy is to be implemented by parent because we do not
   * want to enforce more type information on values
   * 
   * @param other to copy from
   */
  public OdHashedImpl(final OdHashedImpl<? extends T> other) {
    super(other);
    this.odHashed = new HashMap<>(other.odHashed);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(Zone origin, Zone destination) {
    /* hash to single key */
    return odHashed.get(OdHashed.generateHashKey(origin.getId(), destination.getId()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long originId, long destinationId) {
    /* hash to single key */
    return odHashed.get(OdHashed.generateHashKey(originId, destinationId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Zone origin, Zone destination, T value) {
    odHashed.put(OdHashed.generateHashKey(origin.getId(), destination.getId()), value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OdHashedIterator<T> iterator(){
    return new OdHashedIterator<>(this, getOdZones());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OdHashedImpl<T> shallowClone(){
    return new OdHashedImpl<>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public  OdHashedImpl<T> deepClone(){
    LOGGER.severe("No deep copy available of OdHashedImpl instance due to unknown type T, shallow copy instead");
    return shallowClone();
  }

  // getters - setters

}
