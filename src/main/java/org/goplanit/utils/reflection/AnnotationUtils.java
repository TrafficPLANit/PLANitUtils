package org.goplanit.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for the annotations on classes, methods and the other elements reflection gives.
 * <p>
 * Code that checks annotations, such as a test that every settings method carries exactly one of a few annotations,
 * asks the same few questions of many elements; they are answered here once
 * </p>
 *
 * @author markr
 */
public class AnnotationUtils {

  /**
   * Verify if an element carries any of the annotation types given
   *
   * @param element a class, method, field or other annotated element
   * @param annotationTypes to look for, annotations kept at run time
   * @return true when it carries at least one of them, false otherwise, so false when none are given
   */
  public static boolean hasAnyAnnotation(
      AnnotatedElement element, Collection<Class<? extends Annotation>> annotationTypes) {
    return annotationTypes.stream().anyMatch(element::isAnnotationPresent);
  }

  /**
   * The methods a class declares itself, of any access, that carry any of the annotation types given. Inherited
   * methods are left out, and so are the methods the compiler adds, such as the bridge method of an implemented
   * generic method, which carries the annotations of the method it calls
   *
   * @param declaringClass the class
   * @param annotationTypes to look for, annotations kept at run time
   * @return the methods, in no particular order, empty when none
   */
  public static List<Method> getDeclaredMethodsWithAnyAnnotation(
      Class<?> declaringClass, Collection<Class<? extends Annotation>> annotationTypes) {
    return Arrays.stream(declaringClass.getDeclaredMethods())
        .filter(method -> !method.isSynthetic() && hasAnyAnnotation(method, annotationTypes))
        .collect(Collectors.toList());
  }

  /**
   * The nearest class declaring an annotation itself: the class given when it does, otherwise the closest class it
   * extends that does. For an inherited annotation this says where it is written, which a class carrying it does not
   * tell: with {@code @Inherited @Marker} written on {@code Base}, and {@code Sub extends Base}, both carry
   * {@code Marker}, and the nearest class declaring it is {@code Base} for either
   *
   * @param fromClass the class to look up from
   * @param annotationType to look for, an annotation kept at run time
   * @return the class, fromClass itself or one it extends, null when none declares it
   */
  public static Class<?> findNearestClassDeclaring(Class<?> fromClass, Class<? extends Annotation> annotationType) {
    for (Class<?> current = fromClass; current != null; current = current.getSuperclass()) {
      if (current.getDeclaredAnnotation(annotationType) != null) {
        return current;
      }
    }
    return null;
  }
}
