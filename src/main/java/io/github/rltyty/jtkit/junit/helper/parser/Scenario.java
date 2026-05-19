package io.github.rltyty.jtkit.junit.helper.parser;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public interface Scenario {
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
