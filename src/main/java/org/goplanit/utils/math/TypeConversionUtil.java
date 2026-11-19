package org.goplanit.utils.math;

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.UriUtils;
import org.goplanit.utils.reflection.ReflectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

/**
 * some utility functions for converting types easily
 *
 * @author markr
 *
 */
public class TypeConversionUtil {

  /**
   * Parsers of the types {@link #fromString(String, Class, Path)} reads straight from text, keyed by class, a wrapper
   * class standing for its primitive type. Each throws an {@code IllegalArgumentException} for text that does not fit
   */
  private static final Map<Class<?>, Function<String, Object>> TEXT_PARSERS = Map.of(
      String.class, text -> text,
      Boolean.class, TypeConversionUtil::toBooleanStrictly,
      Integer.class, Integer::valueOf,
      Long.class, Long::valueOf,
      Short.class, Short::valueOf,
      Byte.class, Byte::valueOf,
      Double.class, Double::valueOf,
      Float.class, Float::valueOf,
      Number.class, TypeConversionUtil::toNumber);

  /** the text standing for the largest value of a whole number type, such as {@link Integer#MAX_VALUE} for an int */
  private static final String MAX_TEXT = "max";

  /** the largest value of each whole number type, keyed by its wrapper class */
  private static final Map<Class<?>, Number> WHOLE_NUMBER_MAXIMA = Map.of(
      Integer.class, Integer.MAX_VALUE,
      Long.class, Long.MAX_VALUE,
      Short.class, Short.MAX_VALUE,
      Byte.class, Byte.MAX_VALUE);

  /**
   * The constant of an enum whose name is exactly the text
   *
   * @param name of the constant
   * @param enumClass the enum
   * @return the constant
   * @throws IllegalArgumentException when no constant has that name
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Object toEnumConstant(String name, Class<?> enumClass) {
    return Enum.valueOf((Class) enumClass, name);
  }


  /** convert a string that can be interpreted as a long 
   * to a type that parses long via {@code .valueOf(long theLong)}
   * 
   * @param longValue in string form
   * @return the parsed entry in type T
   */
  public static BigInteger toBigInteger(String longValue) {
    return BigInteger.valueOf(Long.parseLong(longValue));
  }
  
  /** convert a string that can be interpreted as a long 
   * to a type that parses long via {@code .valueOf(long theLong)}
   * 
   * @param longValue in string form
   * @return the parsed entry in type T
   */
  public static BigDecimal longStringToBigDecimal(String longValue) {
    return BigDecimal.valueOf(Long.parseLong(longValue));
  }  
  
  /** convert a string that can be interpreted as a double 
   * to a type that parses double via {@code .valueOf(double theDouble)}
   * 
   * @param theDouble in string form
   * @return the parsed entry in type T
   */
  public static BigDecimal doubleStringToBigDecimal(String theDouble) {
    return BigDecimal.valueOf(Double.parseDouble(theDouble));
  }

  /**
   * Text that is exactly true or false as a boolean. Other forms, such as {@code yes}, {@code True} or {@code NO}, are
   * refused rather than read as true or false, so a mistake in a file is noticed
   *
   * @param text to read
   * @return the value
   * @throws IllegalArgumentException for any text other than true or false
   */
  public static Boolean toBooleanStrictly(String text) {
    ExceptionUtils.throwIllegalArgumentIf(!"true".equals(text) && !"false".equals(text),
        "'%s' is neither true nor false", text);
    return Boolean.valueOf(text);
  }

  /**
   * Text of a number as a Long when it is a whole number, and otherwise as a Double. For example, 12 gives a Long and
   * 12.5 a Double, for a value that may be either, such as an id or a distance
   *
   * @param text to read
   * @return the number
   * @throws NumberFormatException when the text is no number
   */
  public static Number toNumber(String text) {
    try {
      return Long.valueOf(text);
    } catch (NumberFormatException e) {
      return Double.valueOf(text);
    }
  }

  /**
   * Text as a value of a type, such as {@code 60.5} as a {@code double}, {@code RAMP_METER} as a constant of an enum,
   * or {@code data/x.pbf} as a path in a folder
   * <p>
   * Useful when the type is only known at run time, such as the type of a method's parameter, found through
   * reflection, that a value read from a file is passed to. It takes text, the primitive types and their wrappers,
   * {@code Number} (a Long for a whole number, otherwise a Double), enums by the exact name of a constant,
   * {@code Path} and {@code File}, and {@code URL}. {@code max} is the largest value of a whole number type, such as
   * {@link Integer#MAX_VALUE} for an int. A path, and a URL without a scheme, is read against the folder when
   * it is relative, so a file can name other files relative to itself. It is strict: only true and false are booleans
   * ({@link #toBooleanStrictly(String)}), and {@code ramp_meter} is not {@code RAMP_METER}. For example:
   * </p>
   * <pre>
   * double reach = TypeConversionUtil.fromString("60.5", double.class, folder);
   * // Integer.MAX_VALUE
   * int largest = TypeConversionUtil.fromString("max", int.class, folder);
   * // C:/settings/data/x.pbf
   * Path input = TypeConversionUtil.fromString("data/x.pbf", Path.class, Path.of("C:/settings"));
   * </pre>
   *
   * @param <T> the type, the wrapper class for a primitive type
   * @param text to convert
   * @param type to convert to, which can be primitive
   * @param folder to read a relative path against, such as {@code Path.of("")} for the working folder
   * @return the value, of the type given, or of its wrapper class for a primitive type
   * @throws IllegalArgumentException when the text does not fit the type, or the type is not one text converts to
   */
  @SuppressWarnings("unchecked")
  public static <T> T fromString(String text, Class<T> type, Path folder) {
    if (type.isEnum()) {
      return (T) toEnumConstant(text, type);
    }
    if (type == Path.class) {
      return (T) FileUtils.resolveAgainstFolder(text, folder);
    }
    if (type == File.class) {
      return (T) FileUtils.resolveAgainstFolder(text, folder).toFile();
    }
    if (type == URL.class) {
      try {
        return (T) UriUtils.fromUriOrPath(text, folder).toURL();
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException(String.format("'%s' is no URL of a known protocol", text), e);
      }
    }
    var boxedType = ReflectionUtils.getBoxedClass(type);
    if (MAX_TEXT.equals(text) && WHOLE_NUMBER_MAXIMA.containsKey(boxedType)) {
      return (T) WHOLE_NUMBER_MAXIMA.get(boxedType);
    }
    var parser = TEXT_PARSERS.get(boxedType);
    ExceptionUtils.throwIllegalArgumentIf(parser == null, "Text does not convert to a %s", type.getName());
    return (T) parser.apply(text);
  }

  /**
   * A value as the text {@link #fromString(String, Class, Path)} reads back as the same value: text as it is, a boolean
   * or number as Java writes it, the largest value of a whole number type as {@code max}, an enum by the name of its
   * constant, a path or folder in full, so it means the same wherever the text is read, and a URL in full. For example,
   * 60.5 gives "60.5", {@link Integer#MAX_VALUE} gives "max", and the path data/x.pbf gives C:/work/data/x.pbf when the
   * program runs in C:/work
   *
   * @param value to give as text
   * @return the text
   * @throws IllegalArgumentException when there is no value, or it is of a type text is not read as
   */
  public static String toString(Object value) {
    ExceptionUtils.throwIllegalArgumentIf(value == null, "No value to give as text");
    if (value instanceof Enum) {
      return ((Enum<?>) value).name();
    }
    if (value instanceof Path) {
      return ((Path) value).toAbsolutePath().toString();
    }
    if (value instanceof File) {
      return ((File) value).getAbsolutePath();
    }
    if (value instanceof URL) {
      return ((URL) value).toExternalForm();
    }
    ExceptionUtils.throwIllegalArgumentIf(!(value instanceof Number) && !TEXT_PARSERS.containsKey(value.getClass()),
        "A %s is not given as text", value.getClass().getName());
    if (value.equals(WHOLE_NUMBER_MAXIMA.get(value.getClass()))) {
      return MAX_TEXT;
    }
    return value.toString();
  }
}
