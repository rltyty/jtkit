/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.parser;

/**
 * Enumeration of supported test data types, covering scalars, lists, 2D lists,
 * and primitive arrays for all primitive wrapper types and {@link String}.
 *
 * <p>Each constant carries its {@link Arity} (how many lines / bracket levels
 * it occupies) and the corresponding Java class used during parsing.
 */
public enum DataType {
  BOOLEAN           (Arity.SINGLE,    Boolean.class),
  BYTE              (Arity.SINGLE,    Byte.class),
  CHARACTER         (Arity.SINGLE,    Character.class),
  SHORT             (Arity.SINGLE,    Short.class),
  INTEGER           (Arity.SINGLE,    Integer.class),
  LONG              (Arity.SINGLE,    Long.class),
  FLOAT             (Arity.SINGLE,    Float.class),
  DOUBLE            (Arity.SINGLE,    Double.class),
  STRING            (Arity.SINGLE,    String.class),

  LIST_BOOLEAN      (Arity.LIST,      Boolean.class),
  LIST_BYTE         (Arity.LIST,      Byte.class),
  LIST_CHARACTER    (Arity.LIST,      Character.class),
  LIST_SHORT        (Arity.LIST,      Short.class),
  LIST_INTEGER      (Arity.LIST,      Integer.class),
  LIST_LONG         (Arity.LIST,      Long.class),
  LIST_FLOAT        (Arity.LIST,      Float.class),
  LIST_DOUBLE       (Arity.LIST,      Double.class),
  LIST_STRING       (Arity.LIST,      String.class),

  LIST_2D_BOOLEAN   (Arity.LIST_2D,   Boolean.class),
  LIST_2D_BYTE      (Arity.LIST_2D,   Byte.class),
  LIST_2D_CHARACTER (Arity.LIST_2D,   Character.class),
  LIST_2D_SHORT     (Arity.LIST_2D,   Short.class),
  LIST_2D_INTEGER   (Arity.LIST_2D,   Integer.class),
  LIST_2D_LONG      (Arity.LIST_2D,   Long.class),
  LIST_2D_FLOAT     (Arity.LIST_2D,   Float.class),
  LIST_2D_DOUBLE    (Arity.LIST_2D,   Double.class),
  LIST_2D_STRING    (Arity.LIST_2D,   String.class),

  ARRAY_BOOLEAN      (Arity.ARRAY,    boolean.class),
  ARRAY_BYTE         (Arity.ARRAY,    byte.class),
  ARRAY_CHARACTER    (Arity.ARRAY,    char.class),
  ARRAY_SHORT        (Arity.ARRAY,    short.class),
  ARRAY_INTEGER      (Arity.ARRAY,    int.class),
  ARRAY_LONG         (Arity.ARRAY,    long.class),
  ARRAY_FLOAT        (Arity.ARRAY,    float.class),
  ARRAY_DOUBLE       (Arity.ARRAY,    double.class),
  ARRAY_STRING       (Arity.ARRAY,    String.class),

  ;

  /**
   * Categorizes a {@link DataType} by its structural shape,
   * used to dispatch to the correct parsing method.
   */
  public enum Arity {
    /** A single scalar value. */
    SINGLE,
    /** A flat {@code List<T>}. */
    LIST,
     /** A 2D {@code List<List<T>>}. */
    LIST_2D,
    /** A primitive array. */
    ARRAY,
  };

  /** The structural shape of this data type. */
  public final Arity arity;
  /** The Java class corresponding to the element type. */
  public final Class<?> clazz;

  DataType(Arity arity, Class<?> clazz) {
    this.arity = arity;
    this.clazz = clazz;
  }

}
