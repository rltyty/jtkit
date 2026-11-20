/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.parser;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

/**
 * Marker interface for test scenarios.
 *
 * <p>Scenario components must be composed exclusively of supported
 * {@code DataType} values. The framework assumes all leaf values are
 * immutable and performs container-level deep copies when reusing cached
 * test data.
 *
 * <p>Supported component shapes include:
 * <ul>
 *   <li>primitive wrapper types and {@link String}</li>
 *   <li>{@code List<T>}</li>
 *   <li>{@code List<List<T>>}</li>
 *   <li>primitive arrays and {@code String[]}</li>
 * </ul>
 */
public interface Scenario {

  /**
   * Returns a human-readable string representation of this scenario,
   * listing each record component by name and value.
   * Used by {@link io.github.rltyty.jtkit.junit.helper.extension.TestCaseCaptor}
   * to annotate failure messages.
   *
   * @return a formatted string of the form {@code name=[value], ...}
   */
  default String toStr() {
    RecordComponent[] components = this.getClass().getRecordComponents();
    StringBuilder sb = new StringBuilder();
    for (RecordComponent rc : components) {
      try {
        Object value = rc.getAccessor().invoke(this);
        if (sb.length() > 0)
          sb.append(", ");
        sb.append(rc.getName()).append("=[").append(arr2Str(value)).append("]");
      } catch (Exception e) {
        sb.append(rc.getName()).append("=[?]");
      }
    }
    return sb.toString();
  }

  private static String arr2Str(Object value) {
    if (value instanceof int[]    v) return Arrays.toString(v);
    if (value instanceof long[]   v) return Arrays.toString(v);
    if (value instanceof double[] v) return Arrays.toString(v);
    if (value instanceof float[]  v) return Arrays.toString(v);
    if (value instanceof boolean[]v) return Arrays.toString(v);
    if (value instanceof byte[]   v) return Arrays.toString(v);
    if (value instanceof short[]  v) return Arrays.toString(v);
    if (value instanceof char[]   v) return Arrays.toString(v);
    if (value instanceof Object[] v) return Arrays.deepToString(v);
    return String.valueOf(value);
  }
}
