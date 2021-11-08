package org.goplanit.utils.od;

import org.goplanit.utils.misc.HashUtils;

/**
 * This interface represents a hashed based implementation for handling origin-demand matrices of a certain type where the data is modelled by a single hash key generated from the
 * OD zone information
 * 
 * @author markr
 *
 */
public interface OdHashed<T> extends OdData<T> {

  /**
   * generate a hash based on origin and destination zone id
   * 
   * @param originZoneId      to use
   * @param destinationZoneId to use
   * @return generated combined hash key
   */
  public static int generateHashKey(long originZoneId, long destinationZoneId) {
    return HashUtils.createCombinedHashCode(originZoneId, destinationZoneId);
  }

  /**
   * Returns an iterator which can iterate through all the origin-destination entries
   * 
   * @return iterator through all available non-empty origin-destination entries
   */
  @Override
  public abstract OdHashedIterator<T> iterator();
}
