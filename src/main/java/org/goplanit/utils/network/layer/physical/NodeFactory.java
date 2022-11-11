package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.locationtech.jts.geom.Point;

/** Factory interface for creating node instances
 * 
 * @author markr
 *
 */
public interface NodeFactory extends GraphEntityFactory<Node> {

  /** Create a new node (without registering)
   * 
   * @return created node
   */
  public abstract Node createNew();

  /**
   * Create a new node (without registering) and set its gemoetry and potentially sync its XML id to the underlying internal id
   *
   * @param position to use
   * @param syncXmlIdToId when true set XML id to internal id, when false leave it null
   * @return created node
   */
  public default Node createNew(Point position, boolean syncXmlIdToId){
    var node = createNew();
    node.setXmlId(node.getId());
    node.setPosition(position);
    return node;
  }
  
  /**
   * Create and register new node
   *
   * @return new node created
   */
  public abstract Node registerNew();

  /**
   * Register a new node and set its geometry and potentially sync its XML id to the underlying internal id
   *
   * @param position to use
   * @param syncXmlIdToId when true set XML id to internal id, when false leave it null
   * @return created and registered node
   */
  public abstract Node registerNew(Point position, boolean syncXmlIdToId);

}
