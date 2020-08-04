package org.planit.utils.id;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
  
  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(IdGenerator.class.getCanonicalName());
  
  /** track unique id's per group and specific class */
  private static Map<IdGroupingToken, LocalIdGenerator> idGroups = new HashMap<IdGroupingToken,LocalIdGenerator>();
   
  /**
   * Create new idGenerators for this group such that we track unique id's within
   * this group
   * 
   * @param group the group for which ids will be generated
   * @return created IdGenerator
   */
  protected static LocalIdGenerator createIdGeneratorForParent(IdGroupingToken group) {
    LocalIdGenerator idGenerator = new LocalIdGenerator();
    idGroups.put(group, idGenerator);
    LOGGER.fine("created id group for" + group.toString());
    return idGroups.get(group);
  }  
  
  /**
   * Generate a unique id for the chosen class
   * 
   * @param group the group for which the id is created
   * @param theClass the class for which the id is being generated
   * @return the generated id
   */
  public static int generateId(IdGroupingToken group, Class<? extends Object> theClass) {
    LocalIdGenerator idGeneratorForGroup = null;
    if (!idGroups.containsKey(group)) {
      idGeneratorForGroup = createIdGeneratorForParent(group);
    } else {
      idGeneratorForGroup = idGroups.get(group);
    }
    return idGeneratorForGroup.generateId(theClass);
  }
  
  /**
   * Reset the id generation at the start of a run
   */
  public static void reset() {
    idGroups.clear();
  }
  
  /** Factory method to create a new id grouping token
   * @param groupDescription description for the group
   * @return idGroupingToken that has been created
   */
  public static IdGroupingToken createIdGroupingToken(String groupDescription) {
    return new IdGroupingToken(groupDescription);
  }
  
  /** Factory method to create a new id grouping token based on the class instance simple name and the instance id
   * This allows for a human readable token as well as a unique token
   * 
   * @param groupOwner group owner
   * @param groupOwnerId id of the group owner
   * @return idGroupingToken that has been created
   */
  public static IdGroupingToken createIdGroupingToken(Object groupOwner, long groupOwnerId) {
    return new IdGroupingToken(groupOwner.getClass().getSimpleName()+"-"+Long.toString(groupOwnerId));
  }  
}
