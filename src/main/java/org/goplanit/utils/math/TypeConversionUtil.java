package org.goplanit.utils.math;

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
   * Parsers of the types {@link #fromText(String, Class, Path)} reads straight from text, keyed by class, a wrapper
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
    if (!"true".equals(text) && !"false".equals(text)) {
      throw new IllegalArgumentException(String.format("'%s' is neither true nor false", text));
    }
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
   * {@code Path} and {@code File}, and {@code URL}. A path, and a URL without a scheme, is read against the folder when
   * it is relative, so a file can name other files relative to itself. It is strict: only true and false are booleans
   * ({@link #toBooleanStrictly(String)}), and {@code ramp_meter} is not {@code RAMP_METER}. For example:
   * </p>
   * <pre>
   * double reach = TypeConversionUtil.fromText("60.5", double.class, folder);
   * // C:/settings/data/x.pbf
   * Path input = TypeConversionUtil.fromText("data/x.pbf", Path.class, Path.of("C:/settings"));
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
  public static <T> T fromText(String text, Class<T> type, Path folder) {
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
    var parser = TEXT_PARSERS.get(ReflectionUtils.getBoxedClass(type));
    if (parser == null) {
      throw new IllegalArgumentException(String.format("Text does not convert to a %s", type.getName()));
    }
    return (T) parser.apply(text);
  }
}
