package org.goplanit.utils;

import org.goplanit.utils.arrays.ArrayUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests of {@link ArrayUtils}: the dot product and the element-wise addition, of whole arrays and of parts of them
 *
 * @author markr
 */
public class ArrayUtilsTest {

  /** 1, 2, 3 from the second value of the first array, by 4, 5, 6 from the start of the second, is 32, as for whole arrays */
  @Test
  public void testDotProductOfParts() {
    assertEquals(32, ArrayUtils.dotProduct(new double[] {9, 1, 2, 3}, 1, new double[] {4, 5, 6, 9}, 0, 3));
    assertEquals(32, ArrayUtils.dotProduct(new double[] {1, 2, 3}, new double[] {4, 5, 6}, 3));
  }

  /** -0.5 times 4 and 6 added to the second and third values of 1, 1, 1, 1 gives 1, -1, -2, 1, the rest untouched */
  @Test
  public void testAddToPartTimesFactor() {
    double[] destination = {1, 1, 1, 1};
    double[] source = {0, 2, 4, 6};
    assertSame(destination, ArrayUtils.addTo(destination, 1, source, 2, -0.5, 2));
    assertArrayEquals(new double[] {1, -1, -2, 1}, destination);
    assertArrayEquals(new double[] {0, 2, 4, 6}, source);
  }

  /** Adding whole arrays: 1, 2 plus 3, 4 is 4, 6 */
  @Test
  public void testAddToWholeArrays() {
    assertArrayEquals(new double[] {4, 6}, ArrayUtils.addTo(new double[] {1, 2}, new double[] {3, 4}, 2));
  }
}
