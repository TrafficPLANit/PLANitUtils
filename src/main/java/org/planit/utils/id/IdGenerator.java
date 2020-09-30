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
   * @param token the group for which ids will be generated
   * @return created IdGenerator
   */
  protected static LocalIdGenerator createIdGeneratorForParent(IdGroupingToken token) {
    LocalIdGenerator idGenerator = new LocalIdGenerator();
    idGroups.put(token, idGenerator);
    LOGGER.fine("created id group for" + token.toString());
    return idGroups.get(token);
  }  
  
  /**
   * Generate a unique id for the chosen class
   * 
   * @param token the group for which the id is created
   * @param theClass the class for which the id is being generated
   * @return the generated id
   */
  public static int generateId(IdGroupingToken token, Class<? extends Object> theClass) {
    LocalIdGenerator idGeneratorForGroup = null;
    if (!idGroups.containsKey(token)) {
      idGeneratorForGroup = createIdGeneratorForParent(token);
    } else {
      idGeneratorForGroup = idGroups.get(token);
    }
    return idGeneratorForGroup.generateId(theClass);
  }
  
  /**
   * Reset the id generation across all registered tokens
   */
  public static void reset() {
    idGroups.clear();
  }

  /**
   * Reset the id generation for a specific token
   * 
   * @param groupId to reset
   */
  public static void reset(IdGroupingToken groupId) {
    if(idGroups.containsKey(groupId)) {
      idGroups.get(groupId).reset();
    }
  }  
  
  /**
   * Reset the id generation for a specific token and class
   * 
   * @param groupId to reset
   * @param theClass to reset
   */
  public static void reset(IdGroupingToken groupId, Class<? extends Object> theClass ) {
    if( idGroups.containsKey(groupId)) {
        idGroups.get(groupId).reset(theClass);
    }
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
  
  /** collect the latest generated id for a given class and token
   * @param token to use
   * @param theClass to use
   * @return latest generated id, -1 if not available
   */
  public static int getLatestIdForToken(IdGroupingToken token, Class<? extends Object> theClass) {
    if (idGroups.containsKey(token)) {
      LocalIdGenerator idGeneratorForGroup = idGroups.get(token);
      if(idGeneratorForGroup != null) {
        return idGeneratorForGroup.getLatestGeneratedId(theClass);
      }
    }
    return -1;
  }


}
