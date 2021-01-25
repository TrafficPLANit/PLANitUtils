package org.planit.utils.zoning;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.EdgeSegment;
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
     */
    public UndirectedConnectoid registerNew(Node accessNode, Zone accessZone, double length) throws PlanItException;       
    
    /**
     * Create new connectoid for a zone and physical access node
     * 
     * @param accessNode node providing access to network layer
     * @param parentZone of the connectoid
     */
    public UndirectedConnectoid registerNew(Node accessNode, Zone parentZone) throws PlanItException;
    
    /**
     * Create new connectoid for a physical access node and leave the connections for access zones for later
     * 
     * @param accessNode node providing access to network layer
     */
    public UndirectedConnectoid registerNew(Node accessNode) throws PlanItException;    
    
    /**
     * Create new connectoid for a zone and physical access node
     * 
     * @param accessEdgeSegment edge segment providing access to network layer
     * @param accessZone of the connectoid 
     * @param length     length of connectoid
     */
    public DirectedConnectoid registerNew(EdgeSegment accessEdgeSegment, Zone accessZone, double length) throws PlanItException;       
    
    /**
     * Create new connectoid for a zone and physical access edge segment
     * 
     * @param accessEdgeSegment edge segment providing access to network layer
     * @param parentZone of the connectoid
     */
    public DirectedConnectoid registerNew(EdgeSegment accessEdgeSegment, Zone parentZone) throws PlanItException;
    
    /**
     * Create new connectoid for a physical access node and leave the connections for access zones for later
     * 
     * @param accessEdgeSegment edge segment providing access to network layer
     */
    public DirectedConnectoid registerNew(EdgeSegment accessEdgeSegment) throws PlanItException;       

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
