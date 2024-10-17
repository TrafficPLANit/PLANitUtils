package org.goplanit.utils.arrays;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * General methods for arrays
 * 
 * @author gman6028, markr
 *
 */
public class ArrayUtils {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(ArrayUtils.class.getCanonicalName());

  /**
   * Add the values of a second array element-wise to the first array
   * 
   * @param destination
   *          the array to be updated
   * @param addToDestination
   *          array of values to be added to destination array
   * @param numberOfElements
   *          number of elements in array to be updated
   * @return destination array
   */
  public static double[] addTo(double[] destination, double[] addToDestination, int numberOfElements) {
    if(addToDestination.length < Math.min(numberOfElements,destination.length)) {
      LOGGER.warning("addToDestination array has less elements than number of elements/destination array to add to, addTo failed");
      return destination;
    }
    
    for (int index = 0; index < numberOfElements; ++index) {
      destination[index] += addToDestination[index];
    }
    return destination;
  }
  
  /**
   * Add the values of a second array element-wise to the first array
   * 
   * @param destination
   *          the array to be updated
   * @param addToDestination
   *          array of values to be added to destination array
   * @return destination array
   */
  public static double[] addTo(double[] destination, double[] addToDestination) {
    if(addToDestination.length < destination.length) {
      LOGGER.warning("addToDestination array has less elements than destination array to add to, addTo failed");
      return destination;
    }
    
    int length = destination.length;
    for (int index = 0; index < length; ++index) {
      destination[index] += addToDestination[index];
    }
    return destination;
  }

  /**
   * Add the values of a second array element-wise to the first array
   *
   * @param origin  the array to subtract from
   * @param subtractFromOrigin array of values to be subtracted from origin array
   * @param destination store result in this array
   * @return destination array
   */
  public static double[] subtractFrom(double[] origin, double[] subtractFromOrigin, double[] destination) {
    int length = subtractFromOrigin.length;
    if(origin.length < destination.length || length < destination.length) {
      LOGGER.warning("elements not compatible with subtractFrom operator");
      return destination;
    }

    for (int index = 0; index < length; ++index) {
      destination[index] = origin[index] - subtractFromOrigin[index];
    }
    return destination;
  }

  /** divide each entry in array by given diviser. When divisor is zero, all entries are set to divideByZeroResult
   * 
   * @param destination array to apply to
   * @param diviser to divide by
   * @param divideByZeroResult result if provided division value is zero
   * @return destination array
   */
  public static double[] divideBy(final double[] destination, double diviser, double divideByZeroResult) {
    if (diviser>0) {
      for (int index = 0; index < destination.length; ++index) {
        destination[index] /= diviser;
      }
    } else {
      for (int index = 0; index < destination.length; ++index) {
        destination[index] = divideByZeroResult;
      }
    }
    return destination;
  }
  
  /**
   * Divide the values of the first array by the the second array (element-wise) 
   * 
   * @param destination the array to be updated
   * @param diviserArray to divide by these values
   * @param divideByZeroResult to use in case the diviser is zero
   * @return destination array
   */
  public static double[] divideBy(double[] destination, double[] diviserArray, double divideByZeroResult) {
    if(diviserArray.length < destination.length) {
      LOGGER.warning("Diviser array has less elements than destination array to divide, divideBy failed");
      return destination;
    }    
    for (int index = 0; index < destination.length; ++index) {
      double divisor = diviserArray[index];
      destination[index] = divisor>0 ? destination[index]/diviserArray[index] : divideByZeroResult;
    }
    return destination;
  }

  /** multiply each entry in array by given multiplicator.
   * 
   * @param destination array to apply to
   * @param multiplicator to multiply with
   * @return destination array
   */
  public static double[] multiplyBy(final double[] destination, double multiplicator) {
    for (int index = 0; index < destination.length; ++index) {
      destination[index] *= multiplicator;
    }
    return destination;
  }  

  /** divide each entry in array by the sum of the entries. When divisor is zero, all entries are set to divideByZeroResult
   * 
   * @param destination array to apply to
   * @param divideByZeroResult result if provided division value is zero
   * @return destination array
   */
  public static double[] divideBySum(double[] destination, int divideByZeroResult) {
    return divideBy(destination, sumOf(destination), divideByZeroResult);
  }

  /** sum of each entry in array
   * 
   * @param array to apply to
   * @return computed sum
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
    return array[findMaxValueIndex(array)];
  }

  /**
   * find the minimum of an array of doubles
   * @param array to check
   * @return maximum value
   */
  public static double getMinimum(double[] array) {
    return array[findMinValueIndex(array)];
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

  /**
   * Simple wrapper around copy of raw array
   * @param original to copy
   * @return shallow copy
   */
    public static double[] shallowCopy(double[] original) {
      return Arrays.copyOf(original, original.length);
    }

    /**
     * Get index with the largest value
     *
     * @param array to check
     * @return index of max value entry
     */
    public static int findMaxValueIndex(double[] array) {
      int length = array.length;
      double maxValue = -Double.MAX_VALUE;
      int maxValueIndex = -1;
      for(int index=0;index<length;++index) {
        if(array[index] > maxValue){
          maxValue = array[index];
          maxValueIndex = index;
        }
      }
      return maxValueIndex;
    }

  /**
   * Get index with the smallest value
   *
   * @param array to check
   * @return index of min value entry
   */
  public static int findMinValueIndex(double[] array) {
    int length = array.length;
    double minValue = Double.MAX_VALUE;
    int minValueIndex = -1;
    for(int index=0;index<length;++index) {
      if(array[index] < minValue){
        minValue = array[index];
        minValueIndex = index;
      }
    }
    return minValueIndex;
  }

  /**
   * Modify array by taking log of each value
   *
   * @param array to apply Math.log to
   */
  public static void logOf(double[] array) {
    for (int index = 0; index < array.length; ++index) {
      array[index] = Math.log(array[index]);
    }
  }

  /**
   * Perform element wise multiplication
   * @param array1 to use
   * @param array2 to use
   * @return result, null if problem found
   */
  public static double[] multiplyElementWise(double[] array1, double[] array2) {
    if(array1 == null){
      LOGGER.warning("Cannot perform element wise multiplication on null array(s)");
      return null;
    }

    if(array1.length != array2.length){
      LOGGER.warning("Cannot perform element wise multiplication on two arrays with different lengths");
      return null;
    }

    double[] result = new double[array1.length];
    for(int index = 0 ; index < array1.length ; ++index){
      result[index] = array1[index] * array2[index];
    }
    return result;
  }
}
