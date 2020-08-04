package org.planit.utils.id;

import java.util.UUID;

/**
 * Class used as token to group ids, i.e., ids for a class generated with this token will be unique within this group.
 * This allows you to have different groups of ids that are unique within each group but not between groups.
 * 
 * An example of when this is useful is if you have multiple instances of container classes (maps, sets) that hold objects that
 * should have unique, continguous ids within this container but NOT across containers, i.e., if a new instances is added to another 
 * container it should not lead to a gap in the ids in the other container.
 * 
 * We use the UUID library to generate the token.
 * 
 * @author markr
 *
 */
public final class IdGroupingToken {
   
  /**
   * the unique token
   */
  private String token;
  
  /**
   * a global token that can be used for ids that are defined globally
   */
  private static final IdGroupingToken GLOBALTOKEN = new IdGroupingToken("global"); 
  
  /** Constructor
   * 
   * @param description of the grouping id, when null empty string is used
   */
  protected IdGroupingToken(String description) {
    if(description==null) {
      description = "";
    }
    String uuid = UUID.randomUUID().toString();
    this.token = description+":"+uuid;
  }
   
  /**
   * collect the string token
   */
  @Override
  public String toString() {
    return this.token;
  }

  /**
   * We have one default global token available for ids that are globally managed rather than within some container.
   * In that case use the global token
   * 
   * @return the global id token
   */
  public static IdGroupingToken collectGlobalToken() {
    return GLOBALTOKEN;
  }
  
  

}
