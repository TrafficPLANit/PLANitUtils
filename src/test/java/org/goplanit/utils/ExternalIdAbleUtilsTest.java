package org.goplanit.utils;

import org.goplanit.utils.id.ExternalIdAbleUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link ExternalIdAbleUtils}
 */
public class ExternalIdAbleUtilsTest {

  /** An optional mapping gives an id only for a key it maps, and never when there is no mapping */
  @Test
  public void hasMappedIdOnlyForAMappedKey() {
    Function<String, String> accessLinkByWaitingArea = Map.of("W", "X")::get;
    assertTrue(ExternalIdAbleUtils.hasMappedId(accessLinkByWaitingArea, "W"));
    assertFalse(ExternalIdAbleUtils.hasMappedId(accessLinkByWaitingArea, "V"));
    assertFalse(ExternalIdAbleUtils.hasMappedId(null, "W"));
  }

  /** A key is mapped to an id only when the mapping gives that id, compared by value */
  @Test
  public void isMappedToIdOnlyForTheIdTheMappingGives() {
    Function<String, String> accessLinkByWaitingArea = waitingArea -> waitingArea.equals("W") ? new String("X") : null;
    assertTrue(ExternalIdAbleUtils.isMappedToId(accessLinkByWaitingArea, "W", "X"));
    assertFalse(ExternalIdAbleUtils.isMappedToId(accessLinkByWaitingArea, "W", "Y"));
    assertFalse(ExternalIdAbleUtils.isMappedToId(accessLinkByWaitingArea, "V", "X"));
    assertFalse(ExternalIdAbleUtils.isMappedToId(null, "W", "X"));
  }
}
