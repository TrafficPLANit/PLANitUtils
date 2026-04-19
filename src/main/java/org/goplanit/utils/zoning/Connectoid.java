package org.goplanit.utils.zoning;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;

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
public interface Connectoid<T extends ConnectoidAccessZoneEntry> extends ExternalIdAble, ManagedId, Iterable<T> {

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
   * The entries that can be accessed by this connectoid by zone id and their data entry
   * 
   * @return accessible entries by zone and type
   */
  public abstract Map<Long, Map<ZoneConnectoidType, T>> getAccessZoneEntriesByType();

  /**
   * The entries that can be accessed by this connectoid for a given ZoneConnectoidType
   *
   * @return accessible entries filtered by type
   */
  public default Stream<T> getAccessZoneEntriesStream(ZoneConnectoidType type){
    return getAccessZoneEntriesByType().values().stream().flatMap(
        e1 -> e1.values().stream()).filter(
            e2 -> e2.getType().equals(type));
  }

  /**
   * The entries by type that can be accessed by this connectoid's access zone
   *
   * @param accessZone to use
   * @return accessible zone entries
   */
  public default Map<ZoneConnectoidType, T> getAccessZoneEntriesByType(Zone accessZone){
    return getAccessZoneEntriesByType().get(accessZone.getId());
  }

  /**
   * Access to access zone listed as a stream
   *
   * @return access zone stream
   */
  public default Stream<? extends Zone> getAccessZoneStream(){
    return getAccessZoneEntriesByType().values().stream().flatMap(
        e -> e.values().stream()).map(ConnectoidAccessZoneEntry::getAccessZone).distinct();
  }

  /**
   * Access to access zone listed as a stream
   *
   * @param type type specifier
   * @return access zone stream
   */
  public default Stream<? extends Zone> getAccessZoneStream(ZoneConnectoidType type){
    return getAccessZoneEntriesByType().values().stream().filter(e -> e.containsKey(type)).map(
        e -> e.get(type).getAccessZone());
  }

  /** Add a new access zone entry with default properties for a given usage type
   * 
   * @param zone to register as accessible
   * @return overwritten zone if any
   */
  public abstract T createAccessZoneEntry(Zone zone, ZoneConnectoidType type);

  /** Remove an existing  access zone entry with default properties for a given usage type
   *
   * @param zone to use
   * @param type to use
   * @return removed entry, null if nothing could be removed
   */
  public abstract T removeAccessZoneEntry(Zone zone, ZoneConnectoidType type);

  /**
   * create entries for all provided access zones
   *
   * @param accessZonesToAdd to add
   */
  public default void createAccessZoneEntries(Collection<? extends Zone> accessZonesToAdd, ZoneConnectoidType type){
    accessZonesToAdd.forEach(z -> createAccessZoneEntry(z, type));
    getAccessZoneEntriesByType();
  }

  /** get access zone entry
   *
   * @param accessZone to verify
   * @param type type specifier
   * @return entry
   */
  public default T getAccessZoneEntry(Zone accessZone, ZoneConnectoidType type){
    return getAccessZoneEntriesByType().get(accessZone.getId()).get(type);
  }

  /** Check if zone is registered as access zone
   *
   * @param accessZone to verify
   * @return true when registered, false otherwise
   */
  public default boolean hasAccessZoneEntry(Zone accessZone){
    if(!getAccessZoneEntriesByType().containsKey(accessZone.getId())){
      return false;
    }
    return !getAccessZoneEntriesByType(accessZone).isEmpty();
  }
  
  /** Check if zone is registered as access zone
   *
   * @param accessZone to verify
   * @param type type specifier
   * @return true when registered, false otherwise
   */
  public default boolean hasAccessZoneEntry(Zone accessZone, ZoneConnectoidType type){
    if(!hasAccessZoneEntry(accessZone)){
      return false;
    }
    return getAccessZoneEntriesByType(accessZone).containsKey(type);
  }
  
  /** first available access zone entry that is accessible based on the first entry the iterator returns
   * 
   * @return first available zone
   */
  public default T getFirstAccessZoneEntry(){
    return getAccessZoneEntriesByType().values().iterator().next().values().iterator().next();
  }

  /**
   * first available access zone entry that is accessible based on the first entry the iterator returns for given type
   *
   * @param type type specifier
   * @return first available zone
   */
  public default Optional<T> getFirstAccessZoneEntry(ZoneConnectoidType type){
    return getAccessZoneEntriesByType().values().stream().flatMap(e ->
        e.values().stream()).filter( e -> e.getType().equals(type)).findFirst();
  }
  
  /** the number of accessible zones registered
   * 
   * @return number of accessible zones
   */
  public default int getNumberOfAccessZoneEntries(){
    return getAccessZoneEntriesByType().size();
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param type type specifier
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, ZoneConnectoidType type, Mode... allowedModes) {
    if(!hasAccessZoneEntry(zone, type)){
      createAccessZoneEntry(zone, type);
    }
    getAccessZoneEntry(zone, type).addAllowedModes(allowedModes);
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param zone to add allowed mode(s) to
   * @param type type specifier
   * @param allowedModes to add
   */
  public default void addAllowedModes(Zone zone, ZoneConnectoidType type, Collection<Mode> allowedModes) {
    if(!hasAccessZoneEntry(zone, type)){
      createAccessZoneEntry(zone, type);
    }
    getAccessZoneEntry(zone, type).addAllowedModes(allowedModes);
  }

  /**
   * Check if mode is allowed for access zone
   *
   * @param accessZone to check
   * @param type type specifier
   * @param mode to check
   * @return true when allowed, false otherwise
   */
  public abstract boolean isModeAllowed(Zone accessZone, ZoneConnectoidType type, Mode mode);

  /**
   * Verify if any of the provided modes is allowed on the access zone connectoid combination
   *
   * @param accessZone to check
   * @param type type specifier
   * @param modes to check
   * @return true if success, false otherwise
   */
  public default boolean isAnyModeAllowed(Zone accessZone, ZoneConnectoidType type, Collection<Mode> modes){
    return modes.stream().anyMatch(m -> isModeAllowed(accessZone, type, m));
  }

  /**
   * Verify which of provided modes is allowed on the access zone connectoid combination
   *
   * @param accessZone to check
   * @param modes to check
   * @return allowed modes subset (if any)
   */
  public default Collection<Mode> getAllowedModesFrom(Zone accessZone, ZoneConnectoidType type, Collection<Mode> modes){
    return getAccessZoneEntry(accessZone, type).getAllowedModesFrom(modes);
  }

  /**
   * Check if explicitly allowed modes are provided
   *
   * @param accessZone to use
   * @param type type specifier
   * @param allowInvalidAccessZone when false and access zone invalid throw PlanitRunTimeException
   * @return flag
   */
  public default boolean hasExplicitlyAllowedModes(
      Zone accessZone, ZoneConnectoidType type, boolean allowInvalidAccessZone){
    if(!hasAccessZoneEntry(accessZone, type)){
      if(allowInvalidAccessZone){
        return false;
      }
      throw new PlanItRunTimeException(String.format("Access zone (%s) for type %s not available for connectoid (%s)",
          accessZone.getIdsAsString(), type, getIdsAsString()));
    }
    return getAccessZoneEntry(accessZone, type).hasExplicitlyAllowedModes();
  }

  /**
   * Get connectoid length for a given access zone
   *
   * @param accessZone to use
   * @return length,if no length or entry exists empty optional is provided
   */
  public default Optional<Double> getLengthKm(Zone accessZone, ZoneConnectoidType type){
    if(!hasAccessZoneEntry(accessZone, type)){
      return Optional.empty();
    }else{
      return getAccessZoneEntry(accessZone, type).getLengthKm();
    }
  }

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
   * recreate the id mapping for the registered access zones in case there is a reason to suspect they
   * got out of sync (interal use only)
   */
   public abstract void recreateAccessZoneIdMapping();

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
