package org.goplanit.utils.id;

import java.util.HashMap;

/**
 * Convenience class to track unique ids across different classes that decide to
 * use a generator for their id members. Ids are generated starting at zero and in an incremental
 * consecutive fashion
 * allowing the placement of objects in raw arrays and indexing them by their id
 * 
 * @author markr
 *
 */
final class LocalIdGenerator {

  /** track unique id's per specific class */
  private HashMap<Class<?>, Long> idTypes = new HashMap<>();

  /**
   * Create a new idGenerator for this type such that we track unique id's within
   * this class
   * 
   * @param theClass
   *          the class for which the id is being generated
   */
  private void createNewIdType(Class<?> theClass) {
    Long initialId = 0L; // choose 0 as this way we can use each id as an index in an array
                         // without
                         // additional effort
    idTypes.put(theClass, initialId);
  }

  /**
   * Generate a unique id for the chosen class
   * 
   * @param theClass
   *          the class for which the id is being generated
   * @return the generated id
   */
  public long generateId(Class<?> theClass) {
    if (!idTypes.containsKey(theClass)) {
      createNewIdType(theClass);
    } else {
      Long id = idTypes.get(theClass);
      id++;
      idTypes.put(theClass, id);
    }
    return idTypes.get(theClass);
  }
  
  /** Collect the latest generated if for the given class (if any)
   * @param theClass for which the id was generated
   * @return latest generated id, -1 when no id has been generated yet for the class
   */
  public long getLatestGeneratedId(Class<?> theClass) {
    if (idTypes.containsKey(theClass)) {
      return idTypes.get(theClass);
    }else {
      return -1;
    }
  }

  /**
   * Reset the id generation for all registered types
   */
  public void reset() {
    idTypes.clear();
  }

  /**
   * Reset the id generation for the given class
   * 
   * @param theClass to reset
   */
  public void reset(Class<?> theClass) {
    idTypes.remove(theClass);
  }

  /**
   * Reset the id for class to the given offset
   * 
   * @param theClass to reset
   * @param offset to use
   */    
  public void resetTo(Class<?> theClass, long offset) {
    idTypes.put(theClass, offset);
  }
}
