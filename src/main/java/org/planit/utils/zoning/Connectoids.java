package org.planit.utils.zoning;

/**
 * container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends Iterable<T>{
  
    /**
     * register a connectoid
     * 
     * If new connectoid overrides an existing connectoid, the removed connectoid is returned
     * 
     * @param connectoid the connectoid to be registered
     * @return connectoid added
     */
    T register(T connectoid);             

    /**
     * Get connectoid by its connectoid type specific id
     * 
     * @param id the connectoid type specific id of this connectoid
     * @return the retrieved connectoid
     */
    public T get(long id);

    /**
     * Return number of connectoids
     * 
     * @return the number of connectoids
     */
    public int size();
}
