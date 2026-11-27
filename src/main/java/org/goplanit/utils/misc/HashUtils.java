package org.goplanit.utils.misc;

import java.util.Arrays;

/**
 * Hashing helpers: combined hash codes for keys, and reproducible random seeds.
 * <p>
 * The seeds let a random draw depend only on what it is about: {@link #createCombinedSeed(long...)} of a fixed seed and
 * the ids of the entities the draw concerns, such as a household and its zone. Adding or dropping one entity, or
 * reading them in another order, then changes none of the other draws. With one random sequence used in turn instead,
 * every draw after the change would shift, and two runs on almost the same input could no longer be compared entity by
 * entity. {@link #mix64(long)} spreads ids that follow each other, such as 7 and 8, to seeds far apart, so neighbouring
 * entities do not get related draws.
 * </p>
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
