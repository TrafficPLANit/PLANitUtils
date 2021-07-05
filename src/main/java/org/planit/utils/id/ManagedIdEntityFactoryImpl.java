package org.planit.utils.id;

import org.planit.utils.id.IdGroupingToken;

/**
 * Base implementation for creating and registering managed id entities and conducting changes to ids based on the factory settings for generating ids.
 * 
 * @author markr
 *
 * @param <E> type of managed id entity
 */
public abstract class ManagedIdEntityFactoryImpl<E extends ManagedId> implements ManagedIdEntityFactory<E> {

  /** the id group token */
  protected IdGroupingToken groupIdToken;

  /**
   * Constructor
   * 
   * @param groupIdToken  to use for creating element ids
   */
  protected ManagedIdEntityFactoryImpl(IdGroupingToken groupIdToken) {
    this.groupIdToken = groupIdToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setIdGroupingToken(IdGroupingToken groupIdToken) {
    this.groupIdToken = groupIdToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IdGroupingToken getIdGroupingToken() {
    return this.groupIdToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E createUniqueCopyOf(ManagedId entityToCopy) {
    /* shallow copy as is */
    @SuppressWarnings("unchecked")
    E copy = (E) entityToCopy.clone();
    /* recreate id and register */
    copy.recreateManagedIds(getIdGroupingToken());
    return copy;
  }

}
