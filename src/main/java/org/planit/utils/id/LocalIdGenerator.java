package org.planit.utils.id;

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
  private HashMap<Class<? extends Object>, Integer> idTypes = new HashMap<Class<? extends Object>, Integer>();

  /**
   * Create a new idGenerator for this type such that we track unique id's within
   * this class
   * 
   * @param theClass
   *          the class for which the id is being generated
   */
  protected void createNewIdType(Class<? extends Object> theClass) {
    Integer initialId = 0; // choose 0 as this way we can use each id as an index in an array
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
  public int generateId(Class<? extends Object> theClass) {
    if (!idTypes.containsKey(theClass)) {
      createNewIdType(theClass);
    } else {
      int id = idTypes.get(theClass);
      id++;
      idTypes.put(theClass, id);
    }
    return idTypes.get(theClass);
  }

  /**
   * Reset the id generation at the start of a run
   */
  public void reset() {
    idTypes.clear();
  }
}
