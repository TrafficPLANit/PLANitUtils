package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MovementUtils {

  /**
   * Drop in replacement for expensive explicit Movement objects. Instead, we generate id mapping for
   * link-to-link allowed/banned movements without creating any instances and whether they are banned or not
   * this is more efficient than expensive maps or explicitly generating all turns, and especially useful when
   * data needs to be attached to turns but we do not want to create these turn objects.
   * <p>
   *   Assumption is here that ordering of entry and exit segments on node DOES NOT CHANGE, if it does we need to
   *   recompute the compiled ids
   * </p>
   * <p>
   *   so for a given turn base don entry exit segment the unique network wide movement id is produced that can be
   *   used to index simulation data easily. If a turn is banned the returned id is -1.
   * </p>
   *
   * @param layers for which the movements hold todo: practically this does not support multiple layers yet
   * @param movements to create compiled ids for, these may just be banned movements or a hybrid
   * @return compiledTurnIds for fast look up functionality and data storage by unique indices given a node and entry
   * exit segment
   */
  public static CompiledMovementIds createCompiledTurnDataIndices(
      UntypedPhysicalNetworkLayers<?> layers, Movements movements){

    Map<Long, NodeMovementTable> nodeMovementMappingTables = new HashMap<>();

    Map<EdgeSegment, Set<EdgeSegment>> bannedByEntryExit = new HashMap<>();
    if(movements != null) {
      bannedByEntryExit = movements.stream().filter(Movement::isBanned).collect(
          Collectors.groupingBy(Movement::getSegmentFrom,
              Collectors.mapping(Movement::getSegmentTo, Collectors.toSet()
              )));
    }
    final var bannedByEntryExitFinal = bannedByEntryExit;

    int[] nextPermissibleTurnId = new int[1]; // raw array trick to avoid requirement of finals in almbda
    layers.stream().flatMap(l -> l.getNodes().stream()).forEach( node ->
    {
      var table = new NodeMovementTable(node);

      long nodeId = node.getId();
      Set<EdgeSegment> bannedExits;
      int inIndex = 0;
      int outIndex = 0;
      for (var inSegment : node.getEntryLinkSegments()) {
        bannedExits = bannedByEntryExitFinal.get(inSegment);
        outIndex = 0;
        for (var outSegment : node.getExitLinkSegments()) {
          if (inSegment.equals(outSegment)){
            ++outIndex;
            continue;
          }

          if(bannedExits!=null && bannedExits.contains(outSegment)){
            ++outIndex;
            continue;
          }

          table.set(inIndex, outIndex, nextPermissibleTurnId[0]++);
          ++outIndex;
        }
        ++inIndex;
      }

      nodeMovementMappingTables.put(nodeId, table);

    });

    return new CompiledMovementIds(nodeMovementMappingTables, nextPermissibleTurnId[0]);
  }
}
