package io.github.rltyty.jtkit.junit.helper.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * getList2D → getList → getSingle → LOW_PARSERS
 */
public abstract class Parser {

  /**
   * public members
   */
  public static Map<DataType.Arity, BiFunction<Parser, Class<?>, Object>> HIGH_PARSERS = Map.of(
      DataType.Arity.SINGLE, (parser, clazz) -> parser.getSingle(clazz),
      DataType.Arity.LIST, (parser, clazz) -> parser.getList(clazz),
      DataType.Arity.LIST_2D, (parser, clazz) -> parser.getList2D(clazz),
      DataType.Arity.ARRAY, (parser, clazz) -> parser.getPrimitiveArray(clazz));

  public abstract <T> T getSingle(Class<T> t);

  public abstract <T> List<T> getList(Class<T> t);

  public abstract <T> List<List<T>> getList2D(Class<T> t);

  public abstract <T> T getPrimitiveArray(Class<?> primitiveType);

  /**
   * protected members
   */

  @SuppressWarnings("unchecked")
  protected static <T> T getPrimitiveArray(String s, Class<?> primitiveType) {
    Class<?> boxed = PRIMITIVE_TO_BOXED.get(primitiveType);
    if (boxed == null) {
      System.err.println("Expected a primitive type but got: " + primitiveType.getName());
      return null;
    }
    List<?> list = getList(s, (Class<Object>) boxed); // ? -> Object to bind E
    if (list == null)
      return null;

    Function<List<?>, Object> converter = BOXED_TO_PRIMITIVE_ARRAY.get(boxed);
    return (T) converter.apply(list);
  }

  protected static <E> List<List<E>> getList2D(String s, Class<E> t) {
    try {
      String content = stripBrackets(s);
      if (content.isBlank())
        return new ArrayList<>();

      List<List<E>> list2D = new ArrayList<>();
      for (String part : splitElements(content)) {
        List<E> inner = getList(part, t);
        if (inner == null) {
          return null;
        }
        list2D.add(inner);
      }
      return list2D;
    } catch (IllegalArgumentException iae) {
      System.err.println("Failed to parse [" + s + "] as a 2D-list of "
          + t.getSimpleName() + ": " + iae.getMessage());
      return null;
    }

  }

  protected static <E> List<E> getList(String s, Class<E> t) {
    try {
      String content = stripBrackets(s);
      if (content.isBlank())
        return new ArrayList<>();

      List<E> list = new ArrayList<>();
      for (String part : splitElements(content)) {
        E e = getSingle(part, t);
        if (e == null) {
          return null;
        }
        list.add(e);
      }
      return list;
    } catch (IllegalArgumentException iae) {
      System.err.println("Failed to parse [" + s + "] as a list of "
          + t.getSimpleName() + ": " + iae.getMessage());
      return null;
    }
  }

  protected static <T> T getSingle(String s, Class<T> t) {
    Function<String, Object> func = LOW_PARSERS.get(t);
    if (func == null) {
      System.err.println("Unsupported type: " + t.getName());
      return null;
    }
    T obj = null;
    try {
      obj = t.cast(func.apply(s));
    } catch (IllegalArgumentException iae) {
      System.err.println("Failed to parse [" + s + "] as "
          + t.getSimpleName() + ": " + iae.getMessage());
    }
    return obj;
  }

  /**
   * private members
   */
  private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = Map.of(
      boolean.class, Boolean.class,
      byte.class, Byte.class,
      char.class, Character.class,
      short.class, Short.class,
      int.class, Integer.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class);

  private static final Map<Class<?>, Function<List<?>, Object>> BOXED_TO_PRIMITIVE_ARRAY = Map.of(
      Integer.class, list -> list.stream().mapToInt(x -> (Integer) x).toArray(),
      Long.class, list -> list.stream().mapToLong(x -> (Long) x).toArray(),
      Double.class, list -> list.stream().mapToDouble(x -> (Double) x).toArray(),
      Boolean.class, list -> {
        boolean[] a = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++)
          a[i] = (Boolean) list.get(i);
        return a;
      },
      Byte.class, list -> {
        byte[] a = new byte[list.size()];
        for (int i = 0; i < list.size(); i++)
          a[i] = (Byte) list.get(i);
        return a;
      },
      Short.class, list -> {
        short[] a = new short[list.size()];
        for (int i = 0; i < list.size(); i++)
          a[i] = (Short) list.get(i);
        return a;
      },
      Float.class, list -> {
        float[] a = new float[list.size()];
        for (int i = 0; i < list.size(); i++)
          a[i] = (Float) list.get(i);
        return a;
      },
      Character.class, list -> {
        char[] a = new char[list.size()];
        for (int i = 0; i < list.size(); i++)
          a[i] = (Character) list.get(i);
        return a;
      });

  /**
   * Function<T, R>
   * ↑ ↑
   * input output
   * e.g. String valueOf
   * These are equivalent:
   * Function<LineParser, Object> fn = LineParser::getBoolean;
   * Function<LineParser, Object> fn = parser -> parser.getBoolean();
   */
  private static final Map<Class<?>, Function<String, Object>> LOW_PARSERS = Map.ofEntries(
      Map.entry(Boolean.class, Boolean::valueOf),
      Map.entry(Byte.class, Byte::valueOf),
      Map.entry(Character.class, Parser::str2Char),
      Map.entry(Short.class, Short::valueOf),
      Map.entry(Integer.class, Integer::valueOf),
      Map.entry(Long.class, Long::valueOf),
      Map.entry(Float.class, Float::valueOf),
      Map.entry(Double.class, Double::valueOf),
      Map.entry(String.class, Parser::stripQuotes));

  /**
   * Extracts content inside outermost [ ].
   * Throws if the input is not a properly bracket-enclosed list.
   */
  private static String stripBrackets(String s)
      throws IllegalArgumentException {
    String stripped = s.strip();
    if (!stripped.startsWith("[") || !stripped.endsWith("]")) {
      throw new IllegalArgumentException("Expected bracket-closed list, "
          + "but got [" + stripped + "].");
    }
    return stripped.substring(1, stripped.length() - 1).strip();
  }

  /**
   * Splits content, e.g. "a, b, [1, 2], c" at top-level commas only.
   */
  private static List<String> splitElements(String s) {
    List<String> elements = new ArrayList<>();
    int depth = 0;
    int start = 0;
    boolean inDoubleQuotes = false;
    boolean inSingleQuotes = false;
    boolean escaped = false;
    for (int i = start; i < s.length(); i++) {
      char c = s.charAt(i);

      if (escaped) {
        escaped = false;
        continue;
      }

      if (c == '\\' && (inDoubleQuotes || inSingleQuotes)) {
        escaped = true;
        continue;
      }
      if (c == '\'' && !inDoubleQuotes) {
        inSingleQuotes = !inSingleQuotes;
        continue;
      }
      if (c == '"' && !inSingleQuotes) {
        inDoubleQuotes = !inDoubleQuotes;
        continue;
      }

      if (!inSingleQuotes && !inDoubleQuotes) {
        if (c == '[')
          depth++;
        else if (c == ']')
          depth--;
        else if (c == ',' && depth == 0) {
          elements.add(s.substring(start, i).strip());
          start = i + 1;
        }
      }

    }
    String tail = s.substring(start).strip();
    if (!tail.isBlank())
      elements.add(tail);
    return elements;
  }

  private static Character str2Char(String s) {
    String trimmed = stripQuotes(s);
    if (trimmed.length() != 1) {
      System.err.println("Expected single character but got : ["
          + trimmed + "].");
    }
    return trimmed.charAt(0);
  }

  private static String stripQuotes(String s) throws IllegalArgumentException {
    if (s == null)
      return null;
    int len = s.length();
    if (len >= 2) {
      char first = s.charAt(0);
      char last = s.charAt(len - 1);

      if ((first == '"' && last == '"') ||
          (first == '\'' && last == '\'')) {
        return s.substring(1, len - 1);
      }
    }
    throw new IllegalArgumentException(
        "String value must be quoted but got: [" + s + "]");
  }

}
