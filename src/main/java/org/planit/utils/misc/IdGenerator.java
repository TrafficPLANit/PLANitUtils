package org.planit.utils.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience class to track unique ids across different classes that decide to
 * use a generator for their id members. Ids are generated starting at zero and in an incremental
 * consecutive fashion. They are unique to their parent though, so if the same class is registered twice with different parents, then their ids will be unique per parent
 * but not across parents.
 * 
 * @author markr
 *
 */
public final class IdGenerator {
  
  /** track unique id's per object and specific class */
  private static Map<Object, LocalIdGenerator> idObjects = new HashMap<Object,LocalIdGenerator>();
  
  /** track unique id's for classes that do not have a parent */
  private static LocalIdGenerator idGenerator = new LocalIdGenerator();
  
  /**
   * Create new idGenerators for this object such that we track unique id's within
   * this object
   * 
   * @param parent the parent object for which ids will be generated
   * @return created IdGenerator
   */
  protected static LocalIdGenerator createIdGeneratorForParent(Object parent) {
    LocalIdGenerator idGenerator = new LocalIdGenerator();
    idObjects.put(parent, idGenerator);
    return idObjects.get(parent);
  }  
  
  /**
   * Generate a unique id for the chosen class
   * 
   * @param parent the parent for which the id is related to 
   * @param theClass
   *          the class for which the id is being generated
   * @return the generated id
   */
  public static int generateId(Object parent, Class<? extends Object> theClass) {
    LocalIdGenerator idGeneratorForParent = null;
    if (!idObjects.containsKey(parent)) {
      idGeneratorForParent = createIdGeneratorForParent(parent);
    } else {
      idGeneratorForParent = idObjects.get(parent);
    }
    return idGeneratorForParent.generateId(theClass);
  }
  
  /**
   * Generate a unique id for the chosen class
   * 
   * @param theClass
   *          the class for which the id is being generated
   * @return the generated id
   */
  public static int generateId(Class<? extends Object> theClass) {
    return idGenerator.generateId(theClass);
  }  

  /**
   * Reset the id generation at the start of a run
   */
  public static void reset() {
    idObjects.clear();
    idGenerator.reset();
  }
}
