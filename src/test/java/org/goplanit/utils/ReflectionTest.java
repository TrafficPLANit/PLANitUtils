package org.goplanit.utils;


import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.reflection.StackWalkerUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the Reflection utils
 * 
 * @author markr
 *
 */
public class ReflectionTest {

  /**
   * A method whose parameters declare the generic types the tests read
   *
   * @param <T> a type with a bound
   * @param plain a plain class
   * @param list a list of text
   * @param map a map whose values are lists
   * @param wildcard a collection of any number type
   * @param raw a list without its type
   * @param bounded a value of the bounded type
   */
  @SuppressWarnings({"rawtypes", "unused"})
  private static <T extends Number> void declaresTypes(
      String plain, List<String> list, Map<Long, List<Integer>> map, Collection<? extends Number> wildcard, List raw,
      T bounded) {
  }

  /**
   * The generic types of the parameters of {@link #declaresTypes}
   *
   * @return the types, in order
   */
  private static Type[] declaredTypes() {
    return Arrays.stream(ReflectionTest.class.getDeclaredMethods())
        .filter(method -> method.getName().equals("declaresTypes")).findFirst().orElseThrow()
        .getGenericParameterTypes();
  }

  /** The raw class of a declared type: the class itself, the class before its type arguments, or the bound */
  @Test
  public void testRawClass() {
    var types = declaredTypes();
    assertEquals(String.class, ReflectionUtils.getRawClass(types[0]));
    assertEquals(List.class, ReflectionUtils.getRawClass(types[1]));
    assertEquals(Map.class, ReflectionUtils.getRawClass(types[2]));
    assertEquals(Collection.class, ReflectionUtils.getRawClass(types[3]));
    assertEquals(List.class, ReflectionUtils.getRawClass(types[4]));
    assertEquals(Number.class, ReflectionUtils.getRawClass(types[5]));
  }

  /** A type argument of a declared type, a wildcard's bound for a wildcard, and Object for a raw type */
  @Test
  public void testTypeArgument() {
    var types = declaredTypes();
    assertEquals(String.class, ReflectionUtils.getTypeArgument(types[1], 0));
    assertEquals(Long.class, ReflectionUtils.getTypeArgument(types[2], 0));
    assertEquals(List.class, ReflectionUtils.getRawClass(ReflectionUtils.getTypeArgument(types[2], 1)));
    assertEquals(Integer.class, ReflectionUtils.getTypeArgument(ReflectionUtils.getTypeArgument(types[2], 1), 0));
    assertEquals(Number.class, ReflectionUtils.getTypeArgument(types[3], 0));
    assertEquals(Object.class, ReflectionUtils.getTypeArgument(types[4], 0));
  }

  /** The wrapper class of a primitive type, and any other class itself */
  @Test
  public void testBoxedClass() {
    assertEquals(Integer.class, ReflectionUtils.getBoxedClass(int.class));
    assertEquals(Boolean.class, ReflectionUtils.getBoxedClass(boolean.class));
    assertEquals(String.class, ReflectionUtils.getBoxedClass(String.class));
  }

  /**
   * Test some of the stackwalker utils
   */
  @Test
  public void theStackWalkerTest() { 
    assertEquals("theStackWalkerTest", StackWalkerUtil.getCallingMethodName());
    assertEquals("org.goplanit.utils.ReflectionTest", StackWalkerUtil.getCallingClassName());
    assertEquals(0, StackWalkerUtil.getCallingMethodType().parameterCount());
  }


}
