package org.goplanit.utils.arrays;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.goplanit.utils.math.Precision;

/**
 * General methods for arrays
 * 
 * @author gman6028, markr
 *
 */
public class ArrayUtils {

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
   * Add the values of a second array element-wise to the first array
   * 
   * @param destination
   *          the array to be updated
   * @param addToDestination
   *          array of values to be added to destination array
   */
  public static void addTo(double[] destination, double[] addToDestination) {
    int length = destination.length;
    for (int index = 0; index < length; ++index) {
      destination[index] += addToDestination[index];
    }
  }  
  
  /** divide each entry in array by given diviser. When divisor is zero, all entries are set to divideByZeroResult
   * 
   * @param destination array to apply to
   * @param diviser to divide by
   * @param divideByZeroResult result if provided division value is zero
   */
  public static void divideBy(final double[] destination, double diviser, double divideByZeroResult) {
    if (Precision.isPositive(diviser)) {
      for (int index = 0; index < destination.length; ++index) {
        destination[index] /= diviser;
      }
    } else {
      for (int index = 0; index < destination.length; ++index) {
        destination[index] = divideByZeroResult;
      }
    }
  }
  
  /** multiply each entry in array by given multiplicator.
   * 
   * @param destination array to apply to
   * @param multiplicator to multiply with
   */
  public static void multiplyBy(final double[] destination, double multiplicator) {
    for (int index = 0; index < destination.length; ++index) {
      destination[index] *= multiplicator;
    }
  }  

  /** divide each entry in array by the sum of the entries. When divisor is zero, all entries are set to divideByZeroResult
   * 
   * @param destination array to apply to
   * @param divideByZeroResult result if provided division value is zero
   */
  public static void divideBySum(double[] destination, int divideByZeroResult) {
    divideBy(destination, sumOf(destination), divideByZeroResult);
  }

  /** sum of each entry in array
   * 
   * @param destination array to apply to
   * @param diviser to divide by
   * @param divideByZeroResult result if provided division value is zero
   */
  public static double sumOf(final double[] array) {
    double sum = 0;
    for (int index = 0; index < array.length; ++index) {
      sum += array[index];
    }
    return sum;
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
  
  /**
   * find the maximum of an array of doubles
   * @param array to check
   * @return maximum value
   */
  public static double getMaximum(double[] array) {
    double max = Double.NEGATIVE_INFINITY;
    for(double entry: array) {
        max = Math.max(max, entry);
    }
    return max;
  }
  
  /**
   * Loop over array entries and apply consumer
   * 
   * @param array to apply consumer to
   * @param consumer to apply
   */
  public static void loopAll(double[][] array, BiConsumer<Integer, Integer> consumer) {
    int length1 = array.length;
    for(int id1=0;id1<length1;++id1) {
      int length2 = array[id1].length;
      for(int id2=0;id2<length2;++id2) {
        consumer.accept(id1, id2);
      }
    }
  }

  /**
   * Loop over array entries and apply consumer
   * 
   * @param array to apply consumer to
   * @param consumer to apply
   */  
  public static void loopAll(double[] array, Consumer<Integer> consumer) {
    int length = array.length;
    for(int id=0;id<length;++id) {
      consumer.accept(id);
    }
  }  
  
  /**
   * Loop over array entries and apply consumer
   *
   * @param <T> type of array contents
   * @param array to apply consumer to
   * @param consumer to apply
   */  
  public static <T> void loopAll(T[] array, Consumer<T> consumer) {
    int length = array.length;
    for(int index=0;index<length;++index) {
      consumer.accept(array[index]);
    }
  }   
}
