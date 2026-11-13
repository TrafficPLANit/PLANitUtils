package org.goplanit.utils;

import org.goplanit.utils.id.ExternalIdAbleImpl;
import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.id.IdMappingUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link IdMappingUtils}
 */
public class IdMappingUtilsTest {

  /**
   * Create an entity with the given ids
   *
   * @param id of the entity
   * @param xmlId of the entity
   * @param externalId of the entity
   * @return created entity
   */
  private static ExternalIdAbleImpl entity(long id, String xmlId, String externalId) {
    var entity = new ExternalIdAbleImpl(id);
    entity.setXmlId(xmlId);
    entity.setExternalId(externalId);
    return entity;
  }

  /** The ids of the chosen type are sorted as text and joined with commas */
  @Test
  public void toSortedCommaSeparatedIds() {
    var entities = List.of(entity(2, "b", "y"), entity(10, "a", "x"));
    assertEquals("10,2", IdMappingUtils.toSortedCommaSeparatedIds(entities, IdMapperType.ID));
    assertEquals("a,b", IdMappingUtils.toSortedCommaSeparatedIds(entities, IdMapperType.XML));
    assertEquals("x,y", IdMappingUtils.toSortedCommaSeparatedIds(entities, IdMapperType.EXTERNAL_ID));
    assertEquals("", IdMappingUtils.toSortedCommaSeparatedIds(List.of(), IdMapperType.ID));
  }
}
