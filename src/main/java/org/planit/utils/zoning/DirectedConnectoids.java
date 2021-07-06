package org.planit.utils.zoning;


/**
 * container and factory class for directed connectoids
 * 
 * @author markr
 *
 */
public interface DirectedConnectoids extends Connectoids<DirectedConnectoid> {

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract DirectedConnectoidFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoids clone(); 
  
}
