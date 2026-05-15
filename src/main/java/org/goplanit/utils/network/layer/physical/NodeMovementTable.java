package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.network.layer.physical.CompiledMovementIds;
import org.goplanit.utils.network.layer.physical.Node;

import java.util.Arrays;

public final class NodeMovementTable {

  private final long[] incoming;
  private final long[] outgoing;

  private final int inSize;
  private final int outSize;

  private final int[] nodeLinkToLinkTurnIdMatrix; // flattened [in * outSize + out]

  private int getOverallIndex(int i, int j) {
    return i * outSize + j;
  }

  private int findIncomingIndex(long linkId) {
    for (int i = 0; i < inSize; i++) {
      if (incoming[i] == linkId) return i;
    }
    return -1;
  }

  private int findOutgoingIndex(long linkId) {
    for (int i = 0; i < outSize; i++) {
      if (outgoing[i] == linkId) return i;
    }
    return -1;
  }

  public NodeMovementTable(Node node) {

    this.incoming = new long[node.getNumberOfEntryEdgeSegments()];
    this.outgoing = new long[node.getNumberOfExitEdgeSegments()];

    this.inSize = incoming.length;
    this.outSize = outgoing.length;
    this.nodeLinkToLinkTurnIdMatrix = new int[inSize * outSize];

    // initialise all as banned
    Arrays.fill(nodeLinkToLinkTurnIdMatrix, CompiledMovementIds.BANNED);
  }

  public int get(long inLink, long outLink) {

    int inIdx = findIncomingIndex(inLink);
    if (inIdx < 0) return CompiledMovementIds.BANNED;

    int outIdx = findOutgoingIndex(outLink);
    if (outIdx < 0) return CompiledMovementIds.BANNED;

    return nodeLinkToLinkTurnIdMatrix[getOverallIndex(inIdx, outIdx)];
  }

  public void set(long inLink, long outLink, int turnId) {

    int inIdx = findIncomingIndex(inLink);
    int outIdx = findOutgoingIndex(outLink);

    if (inIdx < 0 || outIdx < 0) {
      return;
    }

    nodeLinkToLinkTurnIdMatrix[getOverallIndex(inIdx, outIdx)] = turnId;
  }

}
