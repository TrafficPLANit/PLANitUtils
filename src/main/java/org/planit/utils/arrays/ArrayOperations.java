package org.planit.utils.arrays;

import java.util.Arrays;

/**
 * General methods for updating arrays
 * 
 * @author gman6028
 *
 */
public class ArrayOperations {

  /**
   * Add the values of a second array element-wise to the first array
   * 
   * @param destination
   *          the array to be updated
   * @param addToDestination
   *          array of values to be added to destination array
   * @param numberOfElements
   *          number of elements in array to be updated
   */
  public static void addTo(double[] destination, double[] addToDestination, int numberOfElements) {
    for (int index = 0; index < numberOfElements; ++index) {
      destination[index] += addToDestination[index];
    }
  }

  /**
   * Return the dot product of two arrays
   * 
   * The dot product is found by multiplying the elements of each array at each
   * position together and then taking the sum of the multiplied values e.g.
   * a[1]*b[1]+ a[2]*b[2] + a[3]*b[3] etc
   * 
   * @param d1
   *          first array in the dot product
   * @param d2
   *          second array in the dot product
   * @param numberOfElements
   *          number of elements in each array
   * @return the value of the dot product
   */
  public static double dotProduct(double[] d1, double[] d2, int numberOfElements) {
    double sum = 0.0;
    for (int index = 0; index < numberOfElements; ++index) {
      sum += d1[index] * d2[index];
    }
    return sum;
  }
  
  /** add an element to the start of the passed in array. Note that this involves a copy of the original array
   * @param elementToPrepend the element to add to start
   * @param theArray the array to prepend
   * @return the new array
   */
  public static Object[] addtoStart(Object elementToPrepend, Object[] theArray ){
    Object[] newArray = (theArray==null) ? new Object[1] : Arrays.copyOf(theArray, theArray.length + 1);    
    newArray[0] = elementToPrepend;
    System.arraycopy(theArray, 0, newArray, 1, theArray.length);
    return newArray;
  }

}
