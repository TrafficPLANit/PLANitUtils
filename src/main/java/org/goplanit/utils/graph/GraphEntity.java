package org.goplanit.utils.graph;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

/**
 * Placeholder interface for all graph entities. Each graph entity's internal id cannot be directly set
 * but it can be recreated if this is required. 
 * 
 * @author markr
 *
 */
public interface GraphEntity extends ExternalIdAble, ManagedId {
    
 
}
