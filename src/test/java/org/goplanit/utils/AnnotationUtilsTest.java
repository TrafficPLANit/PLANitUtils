package org.goplanit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.goplanit.utils.reflection.AnnotationUtils;
import org.junit.jupiter.api.Test;

/**
 * Test the annotation utils: whether an element carries any of several annotations, the annotations it carries whose
 * definition carries a mark, the methods a class declares itself with any of them, the nearest class declaring an
 * annotation, and the class first annotating a method
 *
 * @author markr
 */
public class AnnotationUtilsTest {

  /** a mark on the definition of an annotation, as the settings annotations carry theirs */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  private @interface Mark {
  }

  /** an annotation the tests look for, its definition carrying the mark */
  @Mark
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.TYPE})
  private @interface First {
  }

  /** another annotation the tests look for */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  private @interface Second {
  }

  /** an annotation nothing carries */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  private @interface Unused {
  }

  /** an annotation of classes that their subclasses inherit */
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  private @interface InheritedMark {
  }

  /**
   * A class with methods carrying none, one or both annotations. Comparing makes the compiler add a bridge method,
   * compareTo(Object), which carries the annotations of compareTo(Annotated) too
   */
  @First
  @InheritedMark
  private static class Annotated implements Comparable<Annotated> {

    @First
    public void first() {
    }

    @Second
    protected void second() {
    }

    @First
    @Second
    private void both() {
    }

    public void plain() {
    }

    @First
    @Override
    public int compareTo(Annotated other) {
      return 0;
    }
  }

  /** A class declaring nothing of its own, its annotated methods all inherited */
  private static class Inheriting extends Annotated {
  }

  /** A class repeating the first annotation on its override of first, and adding it on its override of plain */
  private static class Repeating extends Annotated {

    @First
    @Override
    public void first() {
    }

    @First
    @Override
    public void plain() {
    }
  }

  /** A class below repeating the first annotation on both overrides again */
  private static class RepeatingAgain extends Repeating {

    @First
    @Override
    public void first() {
    }

    @First
    @Override
    public void plain() {
    }
  }

  /**
   * The names of the methods a class declares itself with any of the annotations given, sorted
   *
   * @param declaringClass the class
   * @param annotationTypes to look for
   * @return the names, sorted, a name once per method
   */
  private static List<String> declaredNames(
      Class<?> declaringClass, List<Class<? extends Annotation>> annotationTypes) {
    return AnnotationUtils.getDeclaredMethodsWithAnyAnnotation(declaringClass, annotationTypes).stream()
        .map(Method::getName).sorted().collect(Collectors.toList());
  }

  /**
   * A method or class carries any of the annotations when it carries at least one of them, and none when none given
   *
   * @throws NoSuchMethodException never, the methods asked for being public
   */
  @Test
  public void testHasAnyAnnotation() throws NoSuchMethodException {
    var first = Annotated.class.getMethod("first");
    assertTrue(AnnotationUtils.hasAnyAnnotation(first, List.of(Unused.class, First.class)));
    assertFalse(AnnotationUtils.hasAnyAnnotation(first, List.of(Second.class, Unused.class)));
    var plain = Annotated.class.getMethod("plain");
    assertFalse(AnnotationUtils.hasAnyAnnotation(plain, List.of(First.class, Second.class)));
    assertFalse(AnnotationUtils.hasAnyAnnotation(first, List.of()));
    assertTrue(AnnotationUtils.hasAnyAnnotation(Annotated.class, List.of(First.class)));
  }

  /**
   * The annotations an element carries whose definition carries a mark: of First and Second, only First is marked; an
   * element without marked annotations has none
   *
   * @throws NoSuchMethodException never, the methods asked for being declared
   */
  @Test
  public void testGetAnnotationsMarkedWith() throws NoSuchMethodException {
    var both = Annotated.class.getDeclaredMethod("both");
    assertEquals(List.of(First.class), AnnotationUtils.getAnnotationsMarkedWith(both, Mark.class).stream()
        .map(Annotation::annotationType).collect(Collectors.toList()));
    assertEquals(List.of(), AnnotationUtils.getAnnotationsMarkedWith(Annotated.class.getMethod("plain"), Mark.class));
  }

  /**
   * The methods a class declares itself with any of the annotations, of any access, without the bridge method the
   * compiler adds and without inherited methods
   */
  @Test
  public void testGetDeclaredMethodsWithAnyAnnotation() {
    assertEquals(List.of("both", "compareTo", "first"), declaredNames(Annotated.class, List.of(First.class)));
    assertEquals(List.of("both", "compareTo", "first", "second"),
        declaredNames(Annotated.class, List.of(First.class, Second.class)));
    assertEquals(List.of(), declaredNames(Annotated.class, List.of(Unused.class)));
    assertEquals(List.of(), declaredNames(Inheriting.class, List.of(First.class, Second.class)));
  }

  /**
   * The nearest class declaring an annotation is the class itself or the closest class it extends that writes the
   * annotation on itself, also for an annotation subclasses inherit, which the subclass carries without declaring it
   */
  @Test
  public void testFindNearestClassDeclaring() {
    assertTrue(Inheriting.class.isAnnotationPresent(InheritedMark.class));
    assertEquals(Annotated.class, AnnotationUtils.findNearestClassDeclaring(Inheriting.class, InheritedMark.class));
    assertEquals(Annotated.class, AnnotationUtils.findNearestClassDeclaring(Inheriting.class, First.class));
    assertEquals(Annotated.class, AnnotationUtils.findNearestClassDeclaring(Annotated.class, First.class));
    assertNull(AnnotationUtils.findNearestClassDeclaring(Inheriting.class, Unused.class));
  }

  /**
   * The class first annotating a method is the class all others extend of those whose own method of that name and
   * parameters carries the annotation, the class declaring the method when no class above annotates it, however often
   * overrides repeat the annotation
   *
   * @throws NoSuchMethodException never, the methods asked for being public
   */
  @Test
  public void testFindClassFirstAnnotating() throws NoSuchMethodException {
    assertEquals(Annotated.class,
        AnnotationUtils.findClassFirstAnnotating(Inheriting.class.getMethod("first"), First.class));
    assertEquals(Annotated.class,
        AnnotationUtils.findClassFirstAnnotating(RepeatingAgain.class.getMethod("first"), First.class));
    assertEquals(Repeating.class,
        AnnotationUtils.findClassFirstAnnotating(RepeatingAgain.class.getMethod("plain"), First.class));
    assertEquals(Repeating.class,
        AnnotationUtils.findClassFirstAnnotating(Repeating.class.getMethod("plain"), First.class));
  }
}
