package org.planit.utils.zoning;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.network.physical.LinkSegment;
import org.planit.utils.network.physical.Node;

/**
 * container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids extends Iterable<Connectoid>{
  
    /**
     * register a connectoid
     * 
     * If new connectoid overrides an existing connectoid, the removed connectoid is returned
     * 
     * @param connectoid the connectoid to be registered
     * @return connectoid added
     */
    Connectoid register(Connectoid connectoid);

    /**
     * Create new connectoid for a zone and physical access node
     * 
     * @param accessNode node providing access to network layer
     * @param accessZone of the connectoid 
     * @param length     length of connectoid
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public UndirectedConnectoid registerNew(Node accessNode, Zone accessZone, double length) throws PlanItException;       
    
    /**
     * Create new connectoid for a zone and physical access node
     * 
     * @param accessNode node providing access to network layer
     * @param parentZone of the connectoid
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public UndirectedConnectoid registerNew(Node accessNode, Zone parentZone) throws PlanItException;
    
    /**
     * Create new connectoid for a physical access node and leave the connections for access zones for later
     * 
     * @param accessNode node providing access to network layer
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public UndirectedConnectoid registerNew(Node accessNode) throws PlanItException;    
    
    /**
     * Create new connectoid for a zone and physical access node
     * 
     * @param accessLinkSegment link segment providing access to network layer
     * @param accessZone of the connectoid 
     * @param length     length of connectoid
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone accessZone, double length) throws PlanItException;       
    
    /**
     * Create new connectoid for a zone and physical access edge segment
     * 
     * @param accessLinkSegment link segment providing access to network layer
     * @param parentZone of the connectoid
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone) throws PlanItException;
    
    /**
     * Create new connectoid for a physical access node and leave the connections for access zones for later
     * 
     * @param accessLinkSegment link segment providing access to network layer
     * @return registered connectoid
     * @throws PlanItException thrown if error
     */
    public DirectedConnectoid registerNew(LinkSegment accessLinkSegment) throws PlanItException;       

    /**
     * Get connectoid by id
     * 
     * @param id the id of this connectoid
     * @return the retrieved connectoid
     */
    public Connectoid get(long id);

    /**
     * Return number of connectoids
     * 
     * @return the number of connectoids
     */
    public int size();
}
