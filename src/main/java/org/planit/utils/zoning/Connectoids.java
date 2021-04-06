package org.planit.utils.zoning;

/**
 * container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends Iterable<T>{
  
    /** Remove a connectoid. Use with caution as it invalidates the contiguous nature of connectoid ids.
     * consider recreating the internal ids to avoid this
     * 
     * @param connectoid to remove
     * @param connectoid that has been removed 
     */
    public abstract Connectoid remove(T connectoid);
    
    /** Remove a connectoid. Use with caution as it invalidates the contiguous nature of connectoid ids.
     * consider recreating the internal ids to avoid this
     * 
     * @param connectoidId the connectoid to remove by its internal id (unique across all connectoids of any type)
     * @param connectoid that has been removed 
     */
    public abstract Connectoid remove(long connectoidId);    
  
    /**
     * register a connectoid
     * 
     * If new connectoid overrides an existing connectoid, the removed connectoid is returned
     * 
     * @param connectoid the connectoid to be registered
     * @return connectoid added
     */
    public abstract T register(T connectoid);             

    /**
     * Get connectoid by its connectoid type specific id
     * 
     * @param id the connectoid type specific id of this connectoid
     * @return the retrieved connectoid
     */
    public abstract T get(long id);

    /**
     * Return number of connectoids
     * 
     * @return the number of connectoids
     */
    public abstract int size();
}
