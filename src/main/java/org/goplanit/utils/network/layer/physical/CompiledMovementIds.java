package org.goplanit.utils.network.layer.physical;

import java.util.Map;

public final class CompiledMovementIds {

  public static final int BANNED = -1;

  private final Map<Long, NodeMovementTable> turnIdTablesByNodeId;
  private final int numberOfPermissibleMovements;

  public CompiledMovementIds(
      Map<Long, NodeMovementTable> tables,
      int numberOfPermissibleMovements) {

    this.turnIdTablesByNodeId = tables;
    this.numberOfPermissibleMovements = numberOfPermissibleMovements;
  }

  public int getMovementId(long nodeId, long inLink, long outLink) {

    NodeMovementTable tableMovementIdLookup = turnIdTablesByNodeId.get(nodeId);

    if (tableMovementIdLookup == null){
      return BANNED;
    }

    return tableMovementIdLookup.get(inLink, outLink);
  }

  public int getNumberOfPermissibleMovements() {
    return numberOfPermissibleMovements;
  }
}
