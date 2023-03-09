package org.goplanit.utils.graph;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A bi-consumer that tracks the mapping of an original and deep copy that was created for any graph entity.
 * such mappings are required when dependencies within the copied instances exist that require updating after-the-fact, e.g.,
 * a deep copy of nodes and links where link shave references to nodes. After deep copying both containers, the references to the nodes
 * need updating on the links. this is the mappping that is required to be able to know how to do that
 *
 * @param <T> type of graph entity
 */
public class GraphEntityDeepCopyMapper<T extends GraphEntity> implements BiConsumer<T, T> {

  private final HashMap<T,T> original2DeepCopyMapping = new HashMap<>();

  /**
   *  Track the mapping in internal statefull map
   *
   * @param original the first input argument
   * @param deepCopy the second input argument
   */
  @Override
  public void accept(T original, T deepCopy) {
    original2DeepCopyMapping.put(original, deepCopy);
  }

  /**
   * Collect mapping for the given original
   *
   * @return the mapping
   */
  public T getMapping(T original){
    return original2DeepCopyMapping.get(original);
  }
}
