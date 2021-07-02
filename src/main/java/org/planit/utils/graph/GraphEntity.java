package org.planit.utils.graph;

import org.planit.utils.id.ExternalIdAble;
import org.planit.utils.id.ManagedId;

/**
 * Placeholder interface for all graph entities. Each graph entity's internal id cannot be directly set
 * but it can be recreated if this is required. 
 * 
 * @author markr
 *
 */
public interface GraphEntity extends ExternalIdAble, ManagedId {
    
 
}
