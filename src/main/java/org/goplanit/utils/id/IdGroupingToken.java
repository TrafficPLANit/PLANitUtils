package org.goplanit.utils.id;

import java.util.UUID;

/**
 * Token to group ids: ids for a class generated with this token are unique within this group. So there can be
 * different groups of ids, unique within each group but not between groups. The token is generated with the UUID
 * library.
 * <p>
 * This is useful, for example, for multiple instances of container classes (maps, sets) holding objects that should
 * have unique, contiguous ids within each container but NOT across containers: a new instance added to one container
 * should not leave a gap in the ids of another.
 * </p>
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
  private static final IdGroupingToken GLOBALTOKEN = IdGroupingToken.create("global");
  
  /** Constructor
   * 
   * @param description of the grouping id, when null empty string is used
   */
  IdGroupingToken(String description) {
    if(description==null) {
      description = "";
    }
    String uuid = UUID.randomUUID().toString();
    this.token = description+":"+uuid;
  }

  /**
   * Factory method
   *
   * @param groupDescription to use
   * @return created token
   */
  public static IdGroupingToken create(String groupDescription) {
    return new IdGroupingToken(groupDescription);
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public int hashCode() {
    return token.hashCode();
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public boolean equals(Object obj) {
    return token.equals(obj);
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
