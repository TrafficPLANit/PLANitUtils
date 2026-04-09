package org.goplanit.utils.zoning;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

/**
 * the connecting component between zone(s) and the network.
 * Note that connectoids are not vertices, they merely refer to physical entities via derived interfaces 
 * and the physical network.
 * <p>
 * Each combination of (zone,connectoid) can have additional properties such as length or allowed modes.
 * Not specifying thos will cause the use of defaults (DEFAULT_LENGTH_KM, all modes allowed) 
 * </p>
 * 
 * @author markr
 *
 */
public interface Connectoid<T extends ConnectoidAccessZoneEntry> extends ExternalIdAble, ManagedId, Iterable<Zone> {

  /** the class ot use for id generation */
  public static final Class<Connectoid> CONNECTOID_ID_CLASS = Connectoid.class;

  /** Set the name of the connectoid
   * 
   * @param name its name
   */
  public abstract void setName(String name);  
  
  /** The name of the connectoid
   * 
   * @return its name
   */
  public abstract String getName();

  /**
   * The zones that can be accessed by this connectoid by zone id and their data entry
   * 
   * @return accessible zones
   */
  public abstract Map<Long, T> getAccessZoneEntries();

  /**
   * Access to access zone listed as a stream
   *
   * @return access zone stream
   */
  public default Stream<? extends Zone> getAccessZoneStream(){
    return getAccessZoneEntries().values().stream().map(ConnectoidAccessZoneEntry::getAccessZone);
  }

  /** Add a new access zone entry with default properties
   * 
   * @param zone to register as accessible
   * @return overwritten zone if any
   */
  public abstract T createAccessZoneEntry(Zone zone);

  /**
   * create entries for all provided access zones
   *
   * @param accessZonesToAdd to add
   */
  public default void createAccessZoneEntries(Collection<? extends Zone> accessZonesToAdd){
    accessZonesToAdd.forEach(this::createAccessZoneEntry);
    getAccessZoneEntries();
  }

  /** get access zone entry
   *
   * @param accessZone to verify
   * @return entry
   */
  public abstract T getAccessZoneEntry(Zone accessZone);
  
  /** Check if zone is registered as access zone
   * 
   * @param accessZone to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean hasAccessZoneEntry(Zone accessZone);
  
  /** first available access zone entry that is accessible based on the first entry the iterator returns
   * 
   * @return first available zone
   */
  public abstract T getFirstAccessZoneEntry();
  
  /** the number of accessible zones registered
   * 
   * @return number of accessible zones
   */
  public abstract int getNumberOfAccessZoneEntries();

  /**
   * Set the accessVertex
   *
   * @param accessVertex to use
   */
  public abstract void setAccessVertex(final DirectedVertex accessVertex);

  /** collect the access vertex for this connectoid
   * @return access vertex
   */
  public abstract DirectedVertex getAccessVertex();
  
  /**
   * {@inheritDoc}
   */
  @Override
  default Class<Connectoid> getIdClass() {
    return CONNECTOID_ID_CLASS;
  }

  /** Verify if the connectoid has a name
   * 
   * @return true when present false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** Verify if access zones are registered
   * @return true when present, false otherwise
   */
  public default boolean hasAccessZoneEntries() {
    return getNumberOfAccessZoneEntries()>0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Connectoid<T> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Connectoid<T> deepClone();

}
