package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.layer.physical.LinkFactory;
import org.goplanit.utils.network.layer.physical.Node;

/** Factory interface for creating macroscopic links (that are mode aware via their macroscopic link segments).
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkFactory extends LinkFactory<MacroscopicLink> {
 
  /**
   * Create new macroscopic link on container, allow to be registered on nodes if indicated)
   *
   * @param nodeA           the first node in this link
   * @param nodeB           the second node in this link
   * @param lengthKm length of the link in km
   * @param registerOnNodes choice to register new edge on the vertices or not
   * @return the created link
   */
  public abstract MacroscopicLink registerNew(final Node nodeA, final Node nodeB, double lengthKm, boolean registerOnNodes);
}
