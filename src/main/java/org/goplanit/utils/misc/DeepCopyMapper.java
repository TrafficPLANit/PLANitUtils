package org.goplanit.utils.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A bi-consumer that tracks the mapping of an original to the deep copy created of it. Such mappings are needed when
 * the copied instances refer to each other and must be updated afterwards, e.g., a deep copy of nodes and links,
 * where links refer to nodes: after deep copying both containers, this mapping tells how to update the links'
 * references to the nodes
 *
 * @param <T> type of entity
 */
public class DeepCopyMapper<T> implements BiConsumer<T, T> {

  /**
   * Default constructor
   */
  public DeepCopyMapper(){}

  private final HashMap<T,T> original2DeepCopyMapping = new HashMap<>();

  /**
   *  Track the mapping in internal stateful map
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
   * @param original to get mapping for
   * @return the mapping
   */
  public T getMapping(T original){
    return original2DeepCopyMapping.get(original);
  }

  /** stream of all mappings found
   *
   * @return stream of mapped entries
   **/
  public Stream<Map.Entry<T,T>> stream(){
    return original2DeepCopyMapping.entrySet().stream();
  }
}
