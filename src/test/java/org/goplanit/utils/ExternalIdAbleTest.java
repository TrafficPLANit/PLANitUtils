package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ExternalIdAbleImpl;
import org.goplanit.utils.id.IdMapperType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests of the default methods of {@link ExternalIdAble}
 */
public class ExternalIdAbleTest {

  /** id of the entity in its source */
  private static final String SOURCE_ID = "927869";

  /** internal id of the entity */
  private static final long ID = 5;

  /** Mapped by XML id, the source id is both ids; mapped by external id, the XML id is the internal id */
  @Test
  public void testSetIdsFromSourceId() {
    var byXmlId = new ExternalIdAbleImpl(ID);
    byXmlId.setIdsFromSourceId(SOURCE_ID, IdMapperType.XML);
    assertEquals(SOURCE_ID, byXmlId.getXmlId());
    assertEquals(SOURCE_ID, byXmlId.getExternalId());

    var byExternalId = new ExternalIdAbleImpl(ID);
    byExternalId.setIdsFromSourceId(SOURCE_ID, IdMapperType.EXTERNAL_ID);
    assertEquals(String.valueOf(ID), byExternalId.getXmlId());
    assertEquals(SOURCE_ID, byExternalId.getExternalId());
  }

  /** A source id says nothing about the internal id, so mapping it to the internal id is refused */
  @Test
  public void testSetIdsFromSourceIdRefusesInternalIdMapping() {
    assertThrows(PlanItRunTimeException.class,
        () -> new ExternalIdAbleImpl(ID).setIdsFromSourceId(SOURCE_ID, IdMapperType.ID));
  }
}
