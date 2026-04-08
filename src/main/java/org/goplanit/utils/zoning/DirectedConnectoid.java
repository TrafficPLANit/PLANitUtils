package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * A directed connectoid is referring to one or more access edge segments in a network (layer) which is directed
 * for access hence, the connectoid is also being directed. It is used in situations where not all segments
 * connected to the access node may be available to access the connectoid and may be also allowed only for certain modes
 *
 * @author markr
 *
 */
public interface DirectedConnectoid extends Connectoid{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<DirectedConnectoid> DIRECTED_CONNECTOID_ID_CLASS = DirectedConnectoid.class;

  /** Collect the directed connectoid id
   * 
   * @return directed connectoid id
   */
  public abstract long getDirectedConnectoidId();

  /**
   * The zones that can be accessed by this connectoid
   *
   * @return accessible zones
   */
  @Override
  public abstract Collection<? extends DirectedConnectoidAccessZoneEntry> getAccessZoneEntries();

  /** Add a new access zone entry with default properties
   *
   * @param zone to register as accessible
   * @return overwritten zone if any
   */
  @Override
  public abstract DirectedConnectoidAccessZoneEntry createAccessZoneEntry(Zone zone);

  /** get access zone entry
   *
   * @param accessZone to verify
   * @return entry
   */
  @Override
  public abstract DirectedConnectoidAccessZoneEntry getAccessZoneEntry(Zone accessZone);

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, Mode... allowedModes) {
    if(!hasAccessZoneEntry(zone)){
      createAccessZoneEntry(zone);
    }
    getAccessZoneEntry(zone).addAllowedModes(allowedModes);
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, Collection<Mode> allowedModes) {
    if(!hasAccessZoneEntry(zone)){
      createAccessZoneEntry(zone);
    }
    getAccessZoneEntry(zone).addAllowedModes(allowedModes);
  }

  /**
   * Will collate and produce a stream of all access link segments across all its access
   * zone entries
   *
   * @return access link segments
   */
  public default Stream<LinkSegment> getAccessLinkSegmentsStream(){
    return getAccessZoneEntries().stream().flatMap(e -> e.getAccessLinkSegments().stream());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoid shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedConnectoid deepClone();

  /** set if the node access is downstream or not
   * 
   * @param nodeAccessDownstream true to set it downstream, false otherwise
   */
  public abstract void setNodeAccessDownstream(boolean nodeAccessDownstream);  
  
  /** determine if the node access is downstream or not
   * 
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public abstract boolean isNodeAccessDownstream();
    
  
  /** the class for directed connectoid id generation
   * 
   * @return class to use
   */
  public default Class<DirectedConnectoid> getDirectedConnectoidIdClass(){
    return DIRECTED_CONNECTOID_ID_CLASS;
  }

}
