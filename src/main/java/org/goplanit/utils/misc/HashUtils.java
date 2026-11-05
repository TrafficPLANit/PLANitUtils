package org.goplanit.utils.misc;

import java.util.Arrays;

/** Common location to provide some utilities for common hashing tasks
 * 
 * @author markr
 */
public class HashUtils {

  /** When you want to avoid using nested hash maps and do not need access to the keys themselves, use a combined hash key via this method, 
   * so all can be stored in a single hash map which saves memory.
   * 
   * @param <T> type of the primitive keys
   * @param primitiveKeys to use
   * @return combinedKey
   */
  @SafeVarargs
  public static <T> int createCombinedHashCode(T... primitiveKeys) {
    return Arrays.hashCode(primitiveKeys);
  }

  /**
   * Mix the bits of a value, so that values close to each other give results far apart, with the 64-bit finaliser of
   * MurmurHash3. The same value always gives the same result
   *
   * @param value to mix
   * @return the mixed value
   * @see <a href="https://github.com/aappleby/smhasher">MurmurHash3 (SMHasher)</a>
   */
  public static long mix64(long value) {
    value ^= value >>> 33;
    value *= 0xff51afd7ed558ccdL;
    value ^= value >>> 33;
    value *= 0xc4ceb9fe1a85ec53L;
    value ^= value >>> 33;
    return value;
  }

  /**
   * Create a reproducible random seed from several parts, such as a fixed seed and the ids of what a draw is for. Each
   * part is mixed into the mix of the parts before it, so the seed depends on the order of the parts as well as their
   * values
   *
   * @param parts to create the seed from, in order
   * @return the seed
   */
  public static long createCombinedSeed(long... parts) {
    long seed = 0;
    for (long part : parts) {
      seed = mix64(seed ^ part);
    }
    return seed;
  }
   
}
